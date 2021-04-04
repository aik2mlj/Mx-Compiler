package frontend;

import ast.*;
import util.Position;
import util.Scope;
import util.entity.*;
import util.error.SemanticError;
import util.error.SyntaxError;
import util.type.*;

import java.util.ArrayList;

public class SemanticChecker implements ASTVisitor {
    private Scope globalScope;
    private Scope currentScope;
    private TypeTable typeTable;

    private ArrayList<NewExprNode> globalNewExprs;
    private Position fakePos = new Position(0, 0);
    private String fakeText = "#fuck this";

    public SemanticChecker() {
        typeTable = new TypeTable();
        globalNewExprs = new ArrayList<>();
    }

    public Scope getGlobalScope() {
        return globalScope;
    }

    public TypeTable getTypeTable() {
        return typeTable;
    }

    @Override
    public void visit(ProgramNode node) {
        // new ProgramScope
        globalScope = new Scope(null, Scope.ScopeType.ProgramScope, null, null);
        globalScope.addBuiltInFunction();
        currentScope = globalScope;
        node.setScope(globalScope);

        // step 1: catch all the classes, functions
        var programUnits = node.getProgramUnitNodes();
        for (var it : programUnits) { // get ClassType & add to typeTable
            if (it instanceof ClassDefNode) {
                SingleTypeNode classTypeNode = new SingleTypeNode(it.getPos(), ((ClassDefNode) it).getIdentifier());
                ClassType classType = ((ClassDefNode) it).getClassType();
                typeTable.addType(classTypeNode, classType);
            }
        }
        for (var it : programUnits) { // define functions
            if (it instanceof FuncDefNode) {
                FuncEntity funcEntity = ((FuncDefNode) it).getEntity(FuncEntity.EntityType.Function);
                globalScope.DefineEntity(funcEntity, typeTable);
            }
        }

        // step 2: traverse nodes IN ORDER!
        for (var it : programUnits) {
            it.accept(this);
            if (it instanceof VarNode) {
                if(((VarNode) it).getInitExpr() instanceof NewExprNode) {
                    // set null to globalVar, then "new" in main
                    globalNewExprs.add(((NewExprNode) ((VarNode) it).getInitExpr()));
                    ((VarNode) it).setInitExpr(new NullLiteralNode(fakePos, fakeText));
                }

                VarEntity varEntity = ((VarNode) it).getEntity(VarEntity.EntityType.Global);
                globalScope.DefineEntity(varEntity, typeTable); // define globalVar
            }
        }

        // step 3: check int main()
        FuncEntity mainEntity = globalScope.getFuncEntity("main");
        if (mainEntity != null) {
            if (!((FuncEntity) mainEntity).getTypeNode().getTypeName().equals("int"))
                throw new SemanticError("Return type of function \"main()\" is not int", mainEntity.getPos());
            if (((FuncEntity) mainEntity).getParams().size() != 0)
                throw new SemanticError("Function \"main()\" should have no parameters.", mainEntity.getPos());
        } else {
            throw new SemanticError("Main Function not found", null);
        }
    }

    @Override
    public void visit(SingleTypeNode node) {
        node.setScope(currentScope);
        if (!typeTable.hasType(node))
            throw new SemanticError("Type \"" + node.getTypeName() + "\" not defined", node.getPos());
    }

    @Override
    public void visit(ArrayTypeNode node) {
        // check baseTypeNode
        node.setScope(currentScope);
        node.getBaseTypeNode().accept(this);
    }

    @Override
    public void visit(VoidTypeNode node) {
        node.setScope(currentScope);
    }

    @Override
    public void visit(ClassDefNode node) {
        // new ClassScope
        currentScope = new Scope(globalScope, Scope.ScopeType.ClassScope, null,
                typeTable.getType(new SingleTypeNode(node.getPos(), node.getIdentifier())));
        node.setScope(currentScope);
        // members define & accept
        for (var it : node.getMembers()) {
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(VarEntity.EntityType.Member), typeTable);
        }
        // constructor define
        var constructor = node.getConstructor();
        if (constructor != null) {
            assert constructor.getTypeNode() instanceof VoidTypeNode;
            currentScope.DefineEntity(constructor.getEntity(FuncEntity.EntityType.Constructor), typeTable);
        }
        // methods define
        for (var it : node.getMethods()) {
            if (it.getTypeNode() == null)
                throw new SyntaxError("Method \"" + it.getIdentifier() + "\" has no return type specification", it.getPos());
            currentScope.DefineEntity(it.getEntity(FuncEntity.EntityType.Method), typeTable);
        }
        // constructor accept
        if (constructor != null)
            constructor.accept(this);
        // methods accept
        for (var it : node.getMethods())
            it.accept(this);

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(FuncDefNode node) {
        // new FunctionScope
        currentScope = new Scope(currentScope, Scope.ScopeType.FunctionScope, node.getTypeNode(), currentScope.getClassType());
        node.setScope(currentScope);
        // typeNode
        if (node.hasTypeNode())
            node.getTypeNode().accept(this);
        // params
        FuncEntity function = currentScope.getFuncEntity(node.getIdentifier());
        if (function == null)
            function = currentScope.getConstructorEntity();
        var entityParam = function.getParams();
        for (int i = 0; i < entityParam.size(); ++i) {
            var param = node.getParams().get(i);
            if (param.hasInitExpr())
                throw new SemanticError("Mx do not support parameter value initialization", param.getPos());
            param.accept(this);
            currentScope.DefineEntity(entityParam.get(i), typeTable);
        }
        // --------- Global newExpr
        if (node.getIdentifier().equals("main")) {
            for (NewExprNode globalNewExpr : globalNewExprs) {
                SimpleStmtNode assign = new SimpleStmtNode(fakePos,
                        new AssignExprNode(fakePos, fakeText, globalNewExpr.getLhsExpr(), globalNewExpr));
                node.getSuite().addStmtAtFront(assign);
            }
        }
        // ---------

        // suite: directly accept, since scope has bean changed
        for (var it : node.getSuite().getStatements()) {
            it.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(VarNode node) {
        node.setScope(currentScope);
        // typeNode
        var typeNode = node.getTypeNode();
        if (typeNode.getTypeName().equals("void"))
            throw new SemanticError("Variable type cannot be \"void\"", typeNode.getPos());
        typeNode.accept(this);
        // initExpr
        if (node.hasInitExpr()) {
            var initExpr = node.getInitExpr();
            if(initExpr instanceof NewExprNode)
                ((NewExprNode) initExpr).setLhsExpr(new IdExprNode(node.getPos(), node.getIdentifier(), node.getIdentifier()));
            initExpr.accept(this);
            // check Type
            Type lType = typeTable.getType(node.getTypeNode());
            Type rType = initExpr.getType();
            if (!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + initExpr.getText() + "\"'s type is \"" + rType.getTypeName()
                        + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        }
    }

    @Override
    public void visit(VarListNode node) {
        node.setScope(currentScope);
        // never called
    }

    @Override
    public void visit(BlockStmtNode node) {
        // new BlockScope
        currentScope = new Scope(currentScope, Scope.ScopeType.BlockScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
        node.setScope(currentScope);
        // statements
        for (var it : node.getStatements()) {
            it.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.setScope(currentScope);
        // varNodes
        for (var it : node.getVarNodes()) {
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(VarEntity.EntityType.Local), typeTable); // define variables

            if(it.getInitExpr() instanceof NewExprNode && ((NewExprNode) it.getInitExpr()).hasExpansionBlock())
                ((NewExprNode) it.getInitExpr()).getExpansionBlock().accept(this);
        }
    }

    @Override
    public void visit(IfStmtNode node) {
        node.setScope(currentScope);
        // condition
        var condition = node.getCondition();
        condition.accept(this);
        if (!condition.getType().equals(new BoolType())) // check bool
            throw new SemanticError("Condition is not bool type", condition.getPos());
        // trueStmt
        var trueStmt = node.getTrueStmt();
        if (trueStmt instanceof BlockStmtNode)
            trueStmt.accept(this);
        else {
            currentScope = new Scope(currentScope, Scope.ScopeType.BlockScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
            trueStmt.accept(this);
            currentScope = currentScope.getParentScope();
        }
        // falseStmt
        if (node.hasFalseStmt()) {
            var falseStmt = node.getFalseStmt();
            if (falseStmt instanceof BlockStmtNode)
                falseStmt.accept(this);
            else {
                currentScope = new Scope(currentScope, Scope.ScopeType.BlockScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
                falseStmt.accept(this);
                currentScope = currentScope.getParentScope();
            }
        }
    }

    @Override
    public void visit(ForStmtNode node) {
        node.setScope(currentScope);
        // initExpr
        if (node.hasInitExpr()) {
            node.getInitExpr().accept(this);
        }
        // condition
        if (node.hasCondition()) {
            var condition = node.getCondition();
            condition.accept(this);
            if (!condition.getType().equals(new BoolType())) // check bool
                throw new SemanticError("Condition is not bool type", condition.getPos());
        }
        // increaseExpr
        if (node.hasIncreaseExpr()) {
            node.getIncreaseExpr().accept(this);
        }
        // statement
        currentScope = new Scope(currentScope, Scope.ScopeType.LoopScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
        var statement = node.getStatement();
        if (statement instanceof BlockStmtNode) { // directly accept
            for (var it : ((BlockStmtNode) statement).getStatements())
                it.accept(this);
        } else {
            statement.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(WhileStmtNode node) {
        node.setScope(currentScope);
        var condition = node.getCondition();
        condition.accept(this);
        if (!condition.getType().equals(new BoolType())) // check bool
            throw new SemanticError("Condition is not bool type", condition.getPos());

        // statement
        currentScope = new Scope(currentScope, Scope.ScopeType.LoopScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
        var statement = node.getStatement();
        if (statement instanceof BlockStmtNode) { // directly accept
            for (var it : ((BlockStmtNode) statement).getStatements())
                it.accept(this);
        } else {
            statement.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(ReturnStmtNode node) {
        node.setScope(currentScope);
        // returnExpr
        if (!currentScope.inFunctionScope())
            throw new SemanticError("Return statement appears outside a function scope", node.getPos());
        Type lType = typeTable.getType(currentScope.getFuncReturnTypeNode());
        if (node.hasReturnExpr()) {
            ExprNode returnExpr = node.getReturnExpr();
            returnExpr.accept(this);
            Type rType = returnExpr.getType();
            if (lType instanceof VoidType)
                throw new SemanticError("The function requires no return value", node.getPos());
            if (!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + returnExpr.getText() + "\"'s type is \"" + rType.getTypeName()
                        + "\", which cannot assign to required return type \"" + lType.getTypeName() + "\"", node.getPos());
        } else {
            if (!(lType instanceof VoidType))
                throw new SemanticError("The function requires \"" + lType.getTypeName()
                        + "\" return type but no return value found", node.getPos());
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        node.setScope(currentScope);
        if (!currentScope.inLoopScope())
            throw new SemanticError("Break statement appears outside a loop scope", node.getPos());
    }

    @Override
    public void visit(ContinueStmtNode node) {
        node.setScope(currentScope);
        if (!currentScope.inLoopScope())
            throw new SemanticError("Continue statement appears outside a loop scope", node.getPos());
    }

    @Override
    public void visit(SimpleStmtNode node) {
        node.setScope(currentScope);
        // expression
        if (node.hasExpression()) {
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        node.setScope(currentScope);
        // prefixExpr & memberName
        var prefixExpr = node.getPrefixExpr();
        prefixExpr.accept(this);
        Type prefixType = prefixExpr.getType();
        String memberName = node.getMemberName();
        VarEntity memberEntity;
        if (prefixType instanceof ClassType) {
            if ((memberEntity = ((ClassType) prefixType).getMember(memberName)) == null)
                throw new SemanticError("Class \"" + prefixType.getTypeName() + "\" has no member named \""
                        + memberName + "\"", node.getPos());
        } else
            throw new SemanticError("\"" + prefixExpr.getText() + "\" is not a class", node.getPos());
        // set Type
        node.setType(typeTable.getType(memberEntity.getTypeNode()));
    }

    @Override
    public void visit(MethodExprNode node) {
        node.setScope(currentScope);
        // prefixExpr & methodName
        var prefixExpr = node.getPrefixExpr();
        prefixExpr.accept(this);
        Type prefixType = prefixExpr.getType();
        String methodName = node.getMethodName();
        FuncEntity methodEntity;
        if (prefixType instanceof ClassType) {
            if ((methodEntity = ((ClassType) prefixType).getMethod(methodName)) == null)
                throw new SemanticError("Class \"" + prefixType.getTypeName() + "\" has no method named \""
                        + methodName + "\"", node.getPos());
        } else if (prefixType instanceof ArrayType) {
            if ((methodEntity = ((ArrayType) prefixType).getMethod(methodName)) == null)
                throw new SemanticError("Array type has no method named \""
                        + methodName + "\"", node.getPos());
        } else if (prefixType instanceof StringType) {
            if ((methodEntity = ((StringType) prefixType).getMethod(methodName)) == null)
                throw new SemanticError("String type has no method named \""
                        + methodName + "\"", node.getPos());
        } else
            throw new SemanticError("\"" + prefixExpr.getText() + "\" is not a class/array/string", node.getPos());
        // params
        var paramEntities = methodEntity.getParams();
        var paramExprs = node.getParams();
        if (paramEntities.size() != paramExprs.size())
            throw new SemanticError("Number of parameter(s) mismatches", node.getPos());
        int paramNum = paramEntities.size();
        for (int i = 0; i < paramNum; ++i) {
            var paramExpr = paramExprs.get(i);
            paramExpr.accept(this);
            Type lType = typeTable.getType(paramEntities.get(i).getTypeNode());
            Type rType = paramExpr.getType();
            if (!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + paramExpr.getText() + "\"'s type is \"" + rType.getTypeName()
                        + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        }
        // set Type
        node.setType(typeTable.getType(methodEntity.getTypeNode()));
    }

    @Override
    public void visit(NewExprNode node) {
        node.setScope(currentScope);
        // baseTypeNode
        node.getBaseTypeNode().accept(this);
        // dimension & exprInBrackets
        int dimension = node.getDimension();
        if (dimension == 0) { // class creator
            Type type = typeTable.getType(node.getBaseTypeNode());
            if (type instanceof BasicType)
                throw new SemanticError("Cannot create an instance of basic type \"" + type.getTypeName() + "\"", node.getPos());
            // set Type
            node.setType(type);
        } else { // array creator
            Type baseType = typeTable.getType(node.getBaseTypeNode());
            for (var it : node.getExprInBrackets()) {
                it.accept(this);
                if (!(it.getType() instanceof IntType))
                    throw new SemanticError("Expression \"" + it.getText() + "\" in bracket is not int type", it.getPos());
            }
            // set Type
            node.setType(new ArrayType(baseType, dimension));

            // convert multi-dimensional NewExpr into loops of single NewExpr.
            // this node is modified into a single NewExpr, and a fakeLoopBlock will be added to it as extra
            // info to generate in IRBuilder.
            int subscriptNum = node.getExprInBrackets().size();
            if (subscriptNum > 1) {
                ArrayList<VarNode> fakeLoopVars = new ArrayList<>();
                ArrayList<IdExprNode> subscriptList = new ArrayList<>();
                for (int i = 0; i < subscriptNum - 1; ++i) {
                    fakeLoopVars.add(new VarNode(fakePos, new SingleTypeNode(fakePos, "int"), "_#i" + i,
                            new IntLiteralNode(fakePos, fakeText, 0)));
                    IdExprNode _i = new IdExprNode(fakePos, fakeText, "_#i" + i);
                    subscriptList.add(_i);
                }
                BlockStmtNode fakeLoopBlock = null;
                ForStmtNode forStmt = null;
                for (int i = subscriptNum - 2; i >= 0; --i) {
                    // put forStmt into block
                    // construct newExpr
                    NewExprNode newExpr = new NewExprNode(fakePos, fakeText, node.getBaseTypeNode(),
                            node.getExprInBrackets().get(i + 1), dimension - i - 1);
                    // construct multi subscriptExpr
                    ExprNode subscriptExpr = node.getLhsExpr();
                    for(var subscript: subscriptList) {
                        subscriptExpr = new SubscriptExprNode(fakePos, fakeText, subscriptExpr, subscript);
                    }
                    // construct AssignExpr & add to fakeLoopBlock
                    AssignExprNode assignExpr = new AssignExprNode(fakePos, fakeText, subscriptExpr, newExpr);
                    fakeLoopBlock = new BlockStmtNode(fakePos, new SimpleStmtNode(fakePos, assignExpr));

                    // construct a for loop
                    IdExprNode _i = subscriptList.get(i); // get the last iterator
                    subscriptList.remove(i); // remove the last iterator
                    BinaryExprNode condExpr = new BinaryExprNode(fakePos, fakeText, _i, BinaryExprNode.Operator.Less,
                            node.getExprInBrackets().get(i)); // eg. "i < 4"
                    PrefixExprNode increaseExpr = new PrefixExprNode(fakePos, fakeText,
                            PrefixExprNode.Operator.PrePlus, _i); // "++i"
                    // construct ForStmt
                    fakeLoopBlock.addStmt(forStmt);
                    forStmt = new ForStmtNode(fakePos, null, condExpr, increaseExpr, fakeLoopBlock);
                }
                // define iterators & add forStmt
                fakeLoopBlock = new BlockStmtNode(fakePos, new VarDefStmtNode(fakePos, fakeLoopVars));
                fakeLoopBlock.addStmt(forStmt);
                // change NewExpr, add this fakeLoopBlock to NewExprNode
                node.setOneExprInBracket(node.getExprInBrackets().get(0)); // now it's only a single one.
                node.setExpansionBlock(fakeLoopBlock);

//                System.err.println(node.getExpansionBlock().toString());
            }
        }
    }

    @Override
    public void visit(SubscriptExprNode node) {
        node.setScope(currentScope);
        // nameExpr
        var nameExpr = node.getNameExpr();
        nameExpr.accept(this);
        if (!(nameExpr.getType() instanceof ArrayType))
            throw new SemanticError("\"" + nameExpr.getText() + "\" is not array type", nameExpr.getPos());
        // indexExpr
        var indexExpr = node.getIndexExpr();
        indexExpr.accept(this);
        if (!(indexExpr.getType() instanceof IntType))
            throw new SemanticError("Index \"" + indexExpr.getText() + "\" is not int type", indexExpr.getPos());
        // dimension & set Type
        int dimension = ((ArrayType) nameExpr.getType()).getDimension();
        Type baseType = ((ArrayType) nameExpr.getType()).getBaseType();
        if (dimension == 1)
            node.setType(baseType);
        else
            node.setType(new ArrayType(baseType, dimension - 1));
    }

    @Override
    public void visit(FuncCallExprNode node) {
        node.setScope(currentScope);
        // notice that this can also be a method called in class (without class prefix)
        // funcName
        FuncEntity funcEntity = currentScope.getFuncEntity(node.getFuncName());
        if (funcEntity == null) {
            if (currentScope.inMethodScope())
                throw new SemanticError("Function/method \"" + node.getFuncName() + "\" not found", node.getPos());
            else
                throw new SemanticError("Function \"" + node.getFuncName() + "\" not found", node.getPos());
        }
        // params
        var paramEntities = funcEntity.getParams();
        var paramExprs = node.getParams();
        if (paramEntities.size() != paramExprs.size())
            throw new SemanticError("Number of parameter(s) mismatches", node.getPos());
        int paramNum = paramEntities.size();
        for (int i = 0; i < paramNum; ++i) {
            var paramExpr = paramExprs.get(i);
            paramExpr.accept(this);
            Type lType = typeTable.getType(paramEntities.get(i).getTypeNode());
            Type rType = paramExpr.getType();
            if (!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + paramExpr.getText() + "\"'s type is \"" + rType.getTypeName()
                        + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        }
        // set Type
        node.setType(typeTable.getType(funcEntity.getTypeNode()));
    }

    @Override
    public void visit(SuffixExprNode node) {
        node.setScope(currentScope);
        // exprNode
        var exprNode = node.getExprNode();
        exprNode.accept(this);
        if (!(exprNode.getType() instanceof IntType))
            throw new SemanticError("\"" + exprNode.getText() + "\" is not int type", node.getPos());
        else if (!exprNode.isAssignable())
            throw new SemanticError("\"" + exprNode.getText() + "\" is not assignable", node.getPos());
        // set Type
        node.setType(new IntType());
    }

    @Override
    public void visit(PrefixExprNode node) {
        node.setScope(currentScope);
        // exprNode
        var exprNode = node.getExprNode();
        exprNode.accept(this);
        // operator & type check
        var operator = node.getOperator();
        if (operator == PrefixExprNode.Operator.PrePlus || operator == PrefixExprNode.Operator.PreMinus) {
            // ++a / --a
            if (!(exprNode.getType() instanceof IntType))
                throw new SemanticError("\"" + exprNode.getText() + "\" is not int type", node.getPos());
            else if (!exprNode.isAssignable())
                throw new SemanticError("\"" + exprNode.getText() + "\" is not assignable", node.getPos());
            // set Type
            node.setType(new IntType());
        } else if (operator == PrefixExprNode.Operator.SignPos || operator == PrefixExprNode.Operator.SignNeg
                || operator == PrefixExprNode.Operator.BitwiseNot) {
            // -a / +a
            if (!(exprNode.getType() instanceof IntType))
                throw new SemanticError("\"" + exprNode.getText() + "\" is not int type", node.getPos());
            // set Type
            node.setType(new IntType());
        } else {
            assert operator == PrefixExprNode.Operator.LogicalNot;
            if (!(exprNode.getType() instanceof BoolType))
                throw new SemanticError("\"" + exprNode.getText() + "\" is not bool type", node.getPos());
            // set Type
            node.setType(new BoolType());
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        node.setScope(currentScope);
        // lhsExpr
        var lhsExpr = node.getLhsExpr();
        lhsExpr.accept(this);
        // rhsExpr
        var rhsExpr = node.getRhsExpr();
        rhsExpr.accept(this);
        // operator & type check
        var operator = node.getOperator();
        Type lType = lhsExpr.getType(), rType = rhsExpr.getType();
        switch (operator) {
            // int type operators, return IntType
            case Mul, Div, Mod, Sub, ShiftLeft, ShiftRight, BitwiseAnd, BitwiseXor, BitwiseOr -> {
                if (!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof IntType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int type", lhsExpr.getPos());
                // set Type
                node.setType(new IntType());
            }
            // +: int/string operator, return same
            case Add -> {
                if (!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof IntType) && !(lType instanceof StringType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int/string type", lhsExpr.getPos());
                // set Type
                node.setType(lType);
            }
            // int/string operators, return bool
            case Less, Greater, LessEqual, GreaterEqual -> {
                if (!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof IntType) && !(lType instanceof StringType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int/string type", lhsExpr.getPos());
                // set Type
                node.setType(new BoolType());
            }
            // int/bool/string/array/class/null operators, return bool
            case Equal, NotEqual -> {
                if (!lType.equals(rType)) {
                    if (!((lType instanceof ArrayType) && (rType instanceof NullType)
                            || (lType instanceof NullType) && (rType instanceof ArrayType)
                            || (lType instanceof ClassType) && (rType instanceof NullType)
                            || (lType instanceof NullType) && (rType instanceof ClassType)))
                        throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                } else if (!(lType instanceof IntType) && !(lType instanceof BoolType) && !(lType instanceof StringType)
                        && !(lType instanceof NullType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int/bool/string/null type", lhsExpr.getPos());
                // set Type
                node.setType(new BoolType());
            }
            // bool operators, return bool
            case LogicalAnd, LogicalOr -> {
                if (!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof BoolType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not bool type", lhsExpr.getPos());
                // set Type
                node.setType(new BoolType());
            }
            default -> {
                assert false;
            }
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        node.setScope(currentScope);
        // lhsExpr
        var lhsExpr = node.getLhsExpr();
        lhsExpr.accept(this);
        // rhsExpr
        var rhsExpr = node.getRhsExpr();
        if(rhsExpr instanceof NewExprNode)
            ((NewExprNode) rhsExpr).setLhsExpr(lhsExpr);
        rhsExpr.accept(this);
        // type check
        Type lType = lhsExpr.getType(), rType = rhsExpr.getType();
        if (!lhsExpr.isAssignable())
            throw new SemanticError("\"" + lhsExpr.getText() + "\" is not assignable", node.getPos());
        if (!Type.canAssign(lType, rType))
            throw new SemanticError("\"" + rhsExpr.getText() + "\"'s type is \"" + rType.getTypeName()
                    + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());

        if(rhsExpr instanceof NewExprNode && ((NewExprNode) rhsExpr).hasExpansionBlock())
            ((NewExprNode) rhsExpr).getExpansionBlock().accept(this);
        // set Type
        node.setType(lType); // FIXME

    }

    @Override
    public void visit(ThisExprNode node) {
        node.setScope(currentScope);
        if (!currentScope.inMethodScope())
            throw new SemanticError("\"this\" expression appear outside a method", node.getPos());
        // set Type
        node.setType(currentScope.getClassType());
    }

    @Override
    public void visit(IdExprNode node) {
        node.setScope(currentScope);
        String name = node.getIdentifier();
        VarEntity varEntity = currentScope.getVarEntity(name);
        if (varEntity == null)
            throw new SemanticError("\"" + name + "\" is not a variable reference", node.getPos());
        // set Type
        node.setType(typeTable.getType(varEntity.getTypeNode()));
        node.setVarEntity(varEntity);
    }

    @Override
    public void visit(IntLiteralNode node) {
        node.setScope(currentScope);
        // set Type
        node.setType(new IntType());
    }

    @Override
    public void visit(StringLiteralNode node) {
        node.setScope(currentScope);
        // set Type
        node.setType(new StringType());
    }

    @Override
    public void visit(BoolLiteralNode node) {
        node.setScope(currentScope);
        // set Type
        node.setType(new BoolType());
    }

    @Override
    public void visit(NullLiteralNode node) {
        node.setScope(currentScope);
        // set Type
        node.setType(new NullType());
    }
}
