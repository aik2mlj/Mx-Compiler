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

    public SemanticChecker() {
        typeTable = new TypeTable();
    }

    @Override
    public void visit(ProgramNode node) {
        // new ProgramScope
        globalScope = new Scope(null, Scope.ScopeType.ProgramScope, null, null);
        globalScope.addBuiltInFunction();
        currentScope = globalScope;

        // step 1: catch all the classes, functions
        var programUnits = node.getProgramUnitNodes();
        for(var it: programUnits) { // get ClassType & add to typeTable
            if(it instanceof ClassDefNode) {
                SingleTypeNode classTypeNode = new SingleTypeNode(it.getPos(), ((ClassDefNode) it).getIdentifier());
                ClassType classType = ((ClassDefNode) it).getClassType();
                typeTable.addType(classTypeNode, classType);
            }
        }
        for(var it: programUnits) { // define functions
            if(it instanceof FuncDefNode) {
                FuncEntity funcEntity = ((FuncDefNode) it).getEntity(FuncEntity.EntityType.Function);
                globalScope.DefineEntity(funcEntity, typeTable);
            }
        }

        // step 2: traverse nodes IN ORDER!
        for(var it: programUnits) {
            it.accept(this);
            if(it instanceof VarNode) {
                VarEntity varEntity = ((VarNode) it).getEntity(VarEntity.EntityType.Global);
                globalScope.DefineEntity(varEntity, typeTable); // define globalVar
            }
        }

        // step 3: check int main()
        FuncEntity mainEntity = globalScope.getFuncEntity("main");
        if(mainEntity != null) {
            if(!((FuncEntity) mainEntity).getTypeNode().getTypeName().equals("int"))
                throw new SemanticError("Return type of function \"main()\" is not int", mainEntity.getPos());
            if(((FuncEntity) mainEntity).getParams().size() != 0)
                throw new SemanticError("Function \"main()\" should have no parameters.", mainEntity.getPos());
        } else {
            throw new SemanticError("Main Function not found", null);
        }
    }

    @Override
    public void visit(SingleTypeNode node) {
        if(!typeTable.hasType(node))
            throw new SemanticError("Type \"" + node.getTypeName() + "\" not defined", node.getPos());
    }

    @Override
    public void visit(ArrayTypeNode node) {
        // check baseTypeNode
        node.getBaseTypeNode().accept(this);
    }

    @Override
    public void visit(VoidTypeNode node) {}

    @Override
    public void visit(ClassDefNode node) {
        // new ClassScope
        currentScope = new Scope(globalScope, Scope.ScopeType.ClassScope, null,
                typeTable.getType(new SingleTypeNode(node.getPos(), node.getIdentifier())));
        // members define & accept
        for(var it: node.getMembers()) {
            if(it.hasInitExpr())
                throw new SemanticError("Mx do not support member value initialization", it.getPos());
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(VarEntity.EntityType.Member), typeTable);
        }
        // constructor define
        var constructor = node.getConstructor();
        if(constructor != null) {
            assert constructor.getTypeNode() instanceof VoidTypeNode;
            currentScope.DefineEntity(constructor.getEntity(FuncEntity.EntityType.Constructor), typeTable);
        }
        // methods define
        for(var it: node.getMethods()){
            if(it.getTypeNode() == null)
                throw new SyntaxError("Method \"" + it.getIdentifier() + "\" has no return type specification", it.getPos());
            currentScope.DefineEntity(it.getEntity(FuncEntity.EntityType.Method), typeTable);
        }
        // constructor accept
        if(constructor != null)
            constructor.accept(this);
        // methods accept
        for(var it: node.getMethods())
            it.accept(this);

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(FuncDefNode node) {
        // new FunctionScope
        currentScope = new Scope(currentScope, Scope.ScopeType.FunctionScope, node.getTypeNode(), currentScope.getClassType());
        // typeNode
        if(node.hasTypeNode())
            node.getTypeNode().accept(this);
        // params
        for(var it: node.getParams()) {
            if(it.hasInitExpr())
                throw new SemanticError("Mx do not support parameter value initialization", it.getPos());
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(VarEntity.EntityType.Parameter), typeTable);
        }
        // suite: directly accept, since scope has bean changed
        for(var it: node.getSuite().getStatements()) {
            it.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(VarNode node) {
        // typeNode
        var typeNode = node.getTypeNode();
        if(typeNode.getTypeName().equals("void"))
            throw new SemanticError("Variable type cannot be \"void\"", typeNode.getPos());
        typeNode.accept(this);
        // initExpr
        if(node.hasInitExpr()) {
            var initExpr = node.getInitExpr();
            initExpr.accept(this);
            // check Type
            Type lType = typeTable.getType(node.getTypeNode());
            Type rType = initExpr.getType();
            if(!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + initExpr.getText() + "\"'s type is \""+ rType.getTypeName()
                        + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        }
    }

    @Override
    public void visit(VarListNode node) {
        // never called
    }

    @Override
    public void visit(BlockStmtNode node) {
        // new BlockScope
        currentScope = new Scope(currentScope, Scope.ScopeType.BlockScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
        // statements
        for(var it: node.getStatements()) {
            it.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(VarDefStmtNode node) {
        // varNodes
        for(var it: node.getVarNodes()) {
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(VarEntity.EntityType.Local), typeTable); // define variables
        }
    }

    @Override
    public void visit(IfStmtNode node) {
        // consdition
        var condition = node.getCondition();
        condition.accept(this);
        if(!condition.getType().equals(new BoolType())) // check bool
            throw new SemanticError("Condition is not bool type", condition.getPos());
        // trueStmt
        var trueStmt = node.getTrueStmt();
        if(trueStmt instanceof BlockStmtNode)
            trueStmt.accept(this);
        else {
            currentScope = new Scope(currentScope, Scope.ScopeType.BlockScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
            trueStmt.accept(this);
            currentScope = currentScope.getParentScope();
        }
        // falseStmt
        if(node.hasFalseStmt()) {
            var falseStmt = node.getFalseStmt();
            if(falseStmt instanceof BlockStmtNode)
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
        // initExpr
        if(node.hasInitExpr()) {
            node.getInitExpr().accept(this);
        }
        // condition
        if(node.hasCondition()) {
            var condition = node.getCondition();
            condition.accept(this);
            if(!condition.getType().equals(new BoolType())) // check bool
                throw new SemanticError("Condition is not bool type", condition.getPos());
        }
        // increaseExpr
        if(node.hasIncreaseExpr()) {
            node.getIncreaseExpr().accept(this);
        }
        // statement
        currentScope = new Scope(currentScope, Scope.ScopeType.LoopScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
        var statement = node.getStatement();
        if(statement instanceof BlockStmtNode) { // directly accept
            for(var it : ((BlockStmtNode) statement).getStatements())
                it.accept(this);
        } else {
            statement.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(WhileStmtNode node) {
        var condition = node.getCondition();
        condition.accept(this);
        if(!condition.getType().equals(new BoolType())) // check bool
            throw new SemanticError("Condition is not bool type", condition.getPos());

        // statement
        currentScope = new Scope(currentScope, Scope.ScopeType.LoopScope, currentScope.getFuncReturnTypeNode(), currentScope.getClassType());
        var statement = node.getStatement();
        if(statement instanceof BlockStmtNode) { // directly accept
            for(var it : ((BlockStmtNode) statement).getStatements())
                it.accept(this);
        } else {
            statement.accept(this);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(ReturnStmtNode node) {
        // returnExpr
        if(!currentScope.inFunctionScope())
            throw new SemanticError("Return statement appears outside a function scope", node.getPos());
        Type lType = typeTable.getType(currentScope.getFuncReturnTypeNode());
        if(node.hasReturnExpr()) {
            ExprNode returnExpr = node.getReturnExpr();
            returnExpr.accept(this);
            Type rType = returnExpr.getType();
            if(lType instanceof VoidType)
                throw new SemanticError("The function requires no return value", node.getPos());
            if(!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + returnExpr.getText() + "\"'s type is \""+ rType.getTypeName()
                        + "\", which cannot assign to required return type \"" + lType.getTypeName() + "\"", node.getPos());
        } else {
            if(!(lType instanceof VoidType))
                throw new SemanticError("The function requires \"" + lType.getTypeName()
                        + "\" return type but no return value found", node.getPos());
        }
    }

    @Override
    public void visit(BreakStmtNode node) {
        if(!currentScope.inLoopScope())
            throw new SemanticError("Break statement appears outside a loop scope", node.getPos());
    }

    @Override
    public void visit(ContinueStmtNode node) {
        if(!currentScope.inLoopScope())
            throw new SemanticError("Continue statement appears outside a loop scope", node.getPos());
    }

    @Override
    public void visit(SimpleStmtNode node) {
        // expression
        if(node.hasExpression()) {
            node.getExpression().accept(this);
        }
    }

    @Override
    public void visit(MemberExprNode node) {
        // prefixExpr & memberName
        var prefixExpr = node.getPrefixExpr();
        prefixExpr.accept(this);
        Type prefixType = prefixExpr.getType();
        String memberName = node.getMemberName();
        VarEntity memberEntity;
        if(prefixType instanceof ClassType) {
            if((memberEntity = ((ClassType) prefixType).getMember(memberName)) == null)
                throw new SemanticError("Class \"" + prefixType.getTypeName() + "\" has no member named \""
                        + memberName + "\"", node.getPos());
        } else
            throw new SemanticError("\"" + prefixExpr.getText() + "\" is not a class", node.getPos());
        // set Type
        node.setType(typeTable.getType(memberEntity.getTypeNode()));
    }

    @Override
    public void visit(MethodExprNode node) {
        // prefixExpr & methodName
        var prefixExpr = node.getPrefixExpr();
        prefixExpr.accept(this);
        Type prefixType = prefixExpr.getType();
        String methodName = node.getMethodName();
        FuncEntity methodEntity;
        if(prefixType instanceof ClassType) {
            if((methodEntity = ((ClassType) prefixType).getMethod(methodName)) == null)
                throw new SemanticError("Class \"" + prefixType.getTypeName() + "\" has no method named \""
                        + methodName + "\"", node.getPos());
        } else if(prefixType instanceof ArrayType) {
            if((methodEntity = ((ArrayType) prefixType).getMethod(methodName)) == null)
                throw new SemanticError("Array type has no method named \""
                        + methodName + "\"", node.getPos());
        } else if(prefixType instanceof StringType) {
            if((methodEntity = ((StringType) prefixType).getMethod(methodName)) == null)
                throw new SemanticError("String type has no method named \""
                        + methodName + "\"", node.getPos());
        } else
            throw new SemanticError("\"" + prefixExpr.getText() + "\" is not a class/array/string", node.getPos());
        // params
        var paramEntities = methodEntity.getParams();
        var paramExprs = node.getParams();
        if(paramEntities.size() != paramExprs.size())
            throw new SemanticError("Number of parameter(s) mismatches", node.getPos());
        int paramNum = paramEntities.size();
        for(int i = 0; i < paramNum; ++i) {
            var paramExpr = paramExprs.get(i);
            paramExpr.accept(this);
            Type lType = typeTable.getType(paramEntities.get(i).getTypeNode());
            Type rType = paramExpr.getType();
            if(!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + paramExpr.getText() + "\"'s type is \""+ rType.getTypeName()
                        + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        }
        // set Type
        node.setType(typeTable.getType(methodEntity.getTypeNode()));
    }

    @Override
    public void visit(NewExprNode node) {
        // baseTypeNode
        node.getBaseTypeNode().accept(this);
        // dimension & exprInBrackets
        int dimension = node.getDimension();
        if(dimension == 0) { // class creator
            Type type = typeTable.getType(node.getBaseTypeNode());
            if(type instanceof BasicType)
                throw new SemanticError("Cannot create an instance of basic type \"" + type.getTypeName() + "\"", node.getPos());
            // set Type
            node.setType(type);
        } else { // array creator
            Type baseType = typeTable.getType(node.getBaseTypeNode());
            for(var it: node.getExprInBrackets()) {
                it.accept(this);
                if(!(it.getType() instanceof IntType))
                    throw new SemanticError("Expression \"" + it.getText() + "\" in bracket is not int type", it.getPos());
            }
            // set Type
            node.setType(new ArrayType(baseType, dimension));
        }
    }

    @Override
    public void visit(SubscriptExprNode node) {
        // nameExpr
        var nameExpr = node.getNameExpr();
        nameExpr.accept(this);
        if(!(nameExpr.getType() instanceof ArrayType))
            throw new SemanticError("\"" + nameExpr.getText() + "\" is not array type", nameExpr.getPos());
        // indexExpr
        var indexExpr = node.getIndexExpr();
        indexExpr.accept(this);
        if(!(indexExpr.getType() instanceof IntType))
            throw new SemanticError("Index \"" + indexExpr.getText() + "\" is not int type", indexExpr.getPos());
        // dimension & set Type
        int dimension = ((ArrayType) nameExpr.getType()).getDimension();
        Type baseType = ((ArrayType) nameExpr.getType()).getBaseType();
        if(dimension == 1)
            node.setType(baseType);
        else
            node.setType(new ArrayType(baseType, dimension - 1));
    }

    @Override
    public void visit(FuncCallExprNode node) {
        // notice that this can also be a method called in class (without class prefix)
        // funcName
        FuncEntity funcEntity = currentScope.getFuncEntity(node.getFuncName());
        if(funcEntity == null) {
            if(currentScope.inMethodScope())
                throw new SemanticError("Function/method \"" + node.getFuncName() + "\" not found", node.getPos());
            else
                throw new SemanticError("Function \"" + node.getFuncName() + "\" not found", node.getPos());
        }
        // params
        var paramEntities = funcEntity.getParams();
        var paramExprs = node.getParams();
        if(paramEntities.size() != paramExprs.size())
            throw new SemanticError("Number of parameter(s) mismatches", node.getPos());
        int paramNum = paramEntities.size();
        for(int i = 0; i < paramNum; ++i) {
            var paramExpr = paramExprs.get(i);
            paramExpr.accept(this);
            Type lType = typeTable.getType(paramEntities.get(i).getTypeNode());
            Type rType = paramExpr.getType();
            if(!Type.canAssign(lType, rType))
                throw new SemanticError("\"" + paramExpr.getText() + "\"'s type is \""+ rType.getTypeName()
                        + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        }
        // set Type
        node.setType(typeTable.getType(funcEntity.getTypeNode()));
    }

    @Override
    public void visit(SuffixExprNode node) {
        // exprNode
        var exprNode = node.getExprNode();
        exprNode.accept(this);
        if(!(exprNode.getType() instanceof IntType))
            throw new SemanticError("\"" + exprNode.getText() + "\" is not int type", node.getPos());
        else if(!exprNode.isAssignable())
            throw new SemanticError("\"" + exprNode.getText() + "\" is not assignable", node.getPos());
        // set Type
        node.setType(new IntType());
    }

    @Override
    public void visit(PrefixExprNode node) {
        // exprNode
        var exprNode = node.getExprNode();
        exprNode.accept(this);
        // operator & type check
        var operator = node.getOperator();
        if(operator == PrefixExprNode.Operator.PrePlus || operator == PrefixExprNode.Operator.PreMinus) {
            // ++a / --a
            if(!(exprNode.getType() instanceof IntType))
                throw new SemanticError("\"" + exprNode.getText() + "\" is not int type", node.getPos());
            else if(!exprNode.isAssignable())
                throw new SemanticError("\"" + exprNode.getText() + "\" is not assignable", node.getPos());
            // set Type
            node.setType(new IntType());
        } else if(operator == PrefixExprNode.Operator.SignPos || operator == PrefixExprNode.Operator.SignNeg
                || operator == PrefixExprNode.Operator.BitwiseNot) {
            // -a / +a
            if(!(exprNode.getType() instanceof IntType))
                throw new SemanticError("\"" + exprNode.getText() + "\" is not int type", node.getPos());
            // set Type
            node.setType(new IntType());
        } else {
            assert operator == PrefixExprNode.Operator.LogicalNot;
            if(!(exprNode.getType() instanceof BoolType))
                throw new SemanticError("\"" + exprNode.getText() + "\" is not bool type", node.getPos());
            // set Type
            node.setType(new BoolType());
        }
    }

    @Override
    public void visit(BinaryExprNode node) {
        // lhsExpr
        var lhsExpr = node.getLhsExpr();
        lhsExpr.accept(this);
        // rhsExpr
        var rhsExpr = node.getRhsExpr();
        rhsExpr.accept(this);
        // operator & type check
        var operator = node.getOperator();
        Type lType = lhsExpr.getType(), rType = rhsExpr.getType();
        switch(operator) {
            // int type operators, return IntType
            case Mul, Div, Mod, Sub, ShiftLeft, ShiftRight, BitwiseAnd, BitwiseXor, BitwiseOr -> {
                if(!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof IntType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int type", lhsExpr.getPos());
                // set Type
                node.setType(new IntType());
            }
            // +: int/string operator, return same
            case Add -> {
                if(!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof IntType) && !(lType instanceof StringType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int/string type", lhsExpr.getPos());
                // set Type
                node.setType(lType);
            }
            // int/string operators, return bool
            case Less, Greater, LessEqual, GreaterEqual -> {
                if(!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof IntType) && !(lType instanceof StringType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int/string type", lhsExpr.getPos());
                // set Type
                node.setType(new BoolType());
            }
            // int/bool/string/array/class/null operators, return bool
            case Equal, NotEqual -> {
                if(!lType.equals(rType)) {
                    if(! ((lType instanceof ArrayType) && (rType instanceof NullType)
                            || (lType instanceof NullType) && (rType instanceof ArrayType)
                            || (lType instanceof ClassType) && (rType instanceof NullType)
                            || (lType instanceof NullType) && (rType instanceof ClassType)))
                        throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                } else if (!(lType instanceof IntType) && !(lType instanceof BoolType) && !(lType instanceof StringType)
                        && !(lType instanceof ArrayType) && !(lType instanceof ClassType) && !(lType instanceof NullType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not int/bool/string/array/class/null type", lhsExpr.getPos());
                // set Type
                node.setType(new BoolType());
            }
            // bool operators, return bool
            case LogicalAnd, LogicalOr -> {
                if(!lType.equals(rType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" and \"" + rhsExpr.getText() + "\": type not matches", node.getPos());
                if (!(lType instanceof BoolType))
                    throw new SemanticError("\"" + lhsExpr.getText() + "\" is not bool type", lhsExpr.getPos());
                // set Type
                node.setType(new BoolType());
            }
            default -> { assert false; }
        }
    }

    @Override
    public void visit(AssignExprNode node) {
        // lhsExpr
        var lhsExpr = node.getLhsExpr();
        lhsExpr.accept(this);
        // rhsExpr
        var rhsExpr = node.getRhsExpr();
        rhsExpr.accept(this);
        // type check
        Type lType = lhsExpr.getType(), rType = rhsExpr.getType();
        if(!lhsExpr.isAssignable())
            throw new SemanticError("\"" + lhsExpr.getText() + "\" is not assignable", node.getPos());
        if(!Type.canAssign(lType, rType))
            throw new SemanticError("\"" + rhsExpr.getText() + "\"'s type is \""+ rType.getTypeName()
                    + "\", which cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
        // set Type
        node.setType(lType); // FIXME
    }

    @Override
    public void visit(ThisExprNode node) {
        if(!currentScope.inMethodScope())
            throw new SemanticError("\"this\" expression appear outside a method", node.getPos());
        // set Type
        node.setType(currentScope.getClassType());
    }

    @Override
    public void visit(IdExprNode node) {
        String name = node.getIdentifier();
        VarEntity varEntity = currentScope.getVarEntity(name);
        if(varEntity == null)
            throw new SemanticError("\"" + name + "\" is not a variable reference", node.getPos());
        // set Type
        node.setType(typeTable.getType(varEntity.getTypeNode()));
    }

    @Override
    public void visit(IntLiteralNode node) {
        // set Type
        node.setType(new IntType());
    }

    @Override
    public void visit(StringLiteralNode node) {
        // set Type
        node.setType(new StringType());
    }

    @Override
    public void visit(BoolLiteralNode node) {
        // set Type
        node.setType(new BoolType());
    }

    @Override
    public void visit(NullLiteralNode node) {
        // set Type
        node.setType(new NullType());
    }
}
