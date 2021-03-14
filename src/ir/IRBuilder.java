package ir;

import ast.*;
import ir.instruction.BrInst;
import ir.type.StructType;
import ir.type.VoidType;
import util.Scope;
import util.type.TypeTable;

import java.util.ArrayList;

public class IRBuilder implements ASTVisitor {
    private Module module;

    private Scope globalScope;
    private TypeTable astTypeTable;
    private IRTypeTable irTypeTable;

    private IRFunction currentFunc;
    private IRBlock currentBlock;

    private IRFunction initFunc;

    public IRBuilder(Scope globalScope, TypeTable astTypeTable) {
        // inherit globalScope & astTypeTable from SemanticChecker.
        this.globalScope = globalScope;
        this.astTypeTable = astTypeTable;
        currentFunc = null;
        currentBlock = null;

        // built-in functions added in Module.
        module = new Module(astTypeTable);
        irTypeTable = new IRTypeTable(module, astTypeTable);

        initFunc = new IRFunction(module, "__init__", new VoidType(), new ArrayList<>());
        initFunc.initialize();
        module.addFunction(initFunc);
    }

    @Override
    public void visit(ProgramNode node) {
        // get methodList
        for(var unit: node.getProgramUnitNodes()) {
            if(unit instanceof ClassDefNode) {
                var classType = ((ClassDefNode) unit).getClassType();
                if(((ClassDefNode) unit).hasConstructor())
                    ((ClassDefNode) unit).getConstructor().addMethodToModule(module, classType, astTypeTable, irTypeTable);
                ((ClassDefNode) unit).getMethods().forEach(method -> {
                    method.addMethodToModule(module, classType, astTypeTable, irTypeTable);
                });
            }
        }
        // get functions
        for(var unit: node.getProgramUnitNodes()) {
            if (unit instanceof FuncDefNode)
                ((FuncDefNode) unit).addFunctionToModule(module, astTypeTable, irTypeTable);
        }
        currentFunc = initFunc;
        currentBlock = initFunc.getEntryBlock();
        // declare globalVariable
        for(var unit: node.getProgramUnitNodes()) {
            if(unit instanceof VarNode) unit.accept(this);
        }
        currentBlock.appendInst(new BrInst(currentBlock, null, currentFunc.getRetBlock(), null));
        currentFunc.appendBlock(currentFunc.getRetBlock());

        currentFunc = null;
        currentBlock = null;

        // define classes
        for(var unit: node.getProgramUnitNodes())
            if(unit instanceof ClassDefNode) unit.accept(this);
        for(var unit: node.getProgramUnitNodes()) // define functions
            if(unit instanceof FuncDefNode) unit.accept(this);
    }

    @Override
    public void visit(SingleTypeNode node) {
    }

    @Override
    public void visit(ArrayTypeNode node) {
    }

    @Override
    public void visit(VoidTypeNode node) {
    }

    @Override
    public void visit(ClassDefNode node) {
        module.addStructure(astTypeTable.getType());
    }

    @Override
    public void visit(FuncDefNode node) {

    }

    @Override
    public void visit(VarNode node) {

    }

    @Override
    public void visit(VarListNode node) {

    }

    @Override
    public void visit(BlockStmtNode node) {

    }

    @Override
    public void visit(VarDefStmtNode node) {

    }

    @Override
    public void visit(IfStmtNode node) {

    }

    @Override
    public void visit(ForStmtNode node) {

    }

    @Override
    public void visit(WhileStmtNode node) {

    }

    @Override
    public void visit(ReturnStmtNode node) {

    }

    @Override
    public void visit(BreakStmtNode node) {

    }

    @Override
    public void visit(ContinueStmtNode node) {

    }

    @Override
    public void visit(SimpleStmtNode node) {

    }

    @Override
    public void visit(MemberExprNode node) {

    }

    @Override
    public void visit(MethodExprNode node) {

    }

    @Override
    public void visit(NewExprNode node) {

    }

    @Override
    public void visit(SubscriptExprNode node) {

    }

    @Override
    public void visit(FuncCallExprNode node) {

    }

    @Override
    public void visit(SuffixExprNode node) {

    }

    @Override
    public void visit(PrefixExprNode node) {

    }

    @Override
    public void visit(BinaryExprNode node) {

    }

    @Override
    public void visit(AssignExprNode node) {

    }

    @Override
    public void visit(ThisExprNode node) {

    }

    @Override
    public void visit(IdExprNode node) {

    }

    @Override
    public void visit(IntLiteralNode node) {

    }

    @Override
    public void visit(StringLiteralNode node) {

    }

    @Override
    public void visit(BoolLiteralNode node) {

    }

    @Override
    public void visit(NullLiteralNode node) {

    }
}
