package frontend;

import ast.*;
import util.Position;
import util.Scope;
import util.entity.*;
import util.error.SemanticError;
import util.error.SyntaxError;
import util.type.*;

public class SemanticChecker implements ASTVisitor {
    private Scope globalScope;
    private Scope currentScope;
    private TypeTable typeTable;

    public SemanticChecker() {}

    @Override
    public void visit(ProgramNode node) {
        // new ProgramScope
        globalScope = new Scope(null, Scope.ScopeType.ProgramScope, null, null);
        globalScope.addBuiltInFunction();
        currentScope = globalScope;
        typeTable = new TypeTable();

        // step 1: catch all the classes, functions and globalVars.
        for(var it: node.getClassDefNodes()) { // get ClassType & add to typeTable
            SingleTypeNode classTypeNode = new SingleTypeNode(it.getPos(), it.getIdentifier());
            ClassType classType = it.getClassType();
            typeTable.addType(classTypeNode, classType);
        }
        for(var it: node.getFuncDefNodes()) { // define functions
            FuncEntity funcEntity = it.getEntity(FuncEntity.EntityType.Function);
            globalScope.DefineEntity(funcEntity, typeTable);
        }
        for(var it: node.getGlobalVars()) { // define variables
            VarEntity varEntity = it.getEntity(VarEntity.EntityType.Global);
            globalScope.DefineEntity(varEntity, typeTable);
        }

        // step 2: traverse nodes
        for(var it: node.getClassDefNodes())
            it.accept(this);
        for(var it: node.getFuncDefNodes())
            it.accept(this);
        for(var it: node.getGlobalVars())
            it.accept(this);

        // step 3: check int main()
        Entity mainEntity = globalScope.getEntity("main");
        if(!(mainEntity instanceof FuncEntity)) {
            throw new SemanticError("Main Function not found", null);
        } else {
            if(!((FuncEntity) mainEntity).getTypeNode().getTypeName().equals("int"))
                throw new SemanticError("Return type of function \"main()\" is not int", mainEntity.getPos());
            if(((FuncEntity) mainEntity).getParams().size() != 0)
                throw new SemanticError("Function \"main()\" should have no parameters.", mainEntity.getPos());
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
        // constructor
        var constructor = node.getConstructor();
        if(constructor.getTypeNode() != null)
            throw new SyntaxError("Constructor should have no return value", constructor.getPos());
        constructor.accept(this);
        currentScope.DefineEntity(constructor.getEntity(FuncEntity.EntityType.Constructor), typeTable);
        // members
        for(var it: node.getMembers()) {
            if(it.hasInitExpr())
                throw new SemanticError("Mx do not support member value initialization", it.getPos());
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(VarEntity.EntityType.Member), typeTable);
        }
        // methods
        for(var it: node.getMethods()){
            if(it.getTypeNode() == null)
                throw new SyntaxError("Method \"" + it.getIdentifier() + "\" has no return type specification", it.getPos());
            it.accept(this);
            currentScope.DefineEntity(it.getEntity(FuncEntity.EntityType.Method), typeTable);
        }

        currentScope = currentScope.getParentScope();
    }

    @Override
    public void visit(FuncDefNode node) {
        // new FunctionScope
        currentScope = new Scope(globalScope, Scope.ScopeType.FunctionScope, node.getTypeNode(), currentScope.getClassType());
        // typeNode
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
                throw new SemanticError(rType.getTypeName() + " cannot assign to type \"" + lType.getTypeName() + "\"", node.getPos());
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
}
