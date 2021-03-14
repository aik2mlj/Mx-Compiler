package ast;

public interface ASTVisitor {
    // --------- RootNode ----------
    void visit(ProgramNode node);

    // --------- TypeNode ----------
    void visit(SingleTypeNode node);
    void visit(ArrayTypeNode node);
    void visit(VoidTypeNode node);

    // --------- ProgramUnitNode ----------
    void visit(ClassDefNode node);
    void visit(FuncDefNode node);

    void visit(VarNode node); // versatile node: literally type + ID
    void visit(VarListNode node); // same

    // --------- StmtNode ----------
    void visit(BlockStmtNode node);
    void visit(VarDefStmtNode node);
    void visit(IfStmtNode node);
    void visit(ForStmtNode node);
    void visit(WhileStmtNode node);
    void visit(ReturnStmtNode node);
    void visit(BreakStmtNode node);
    void visit(ContinueStmtNode node);
    void visit(SimpleStmtNode node); // Expr | empty

    // --------- ExprNode ----------
    void visit(MemberExprNode node);
    void visit(MethodExprNode node);
    void visit(NewExprNode node);
    void visit(SubscriptExprNode node);
    void visit(FuncCallExprNode node);
    void visit(SuffixExprNode node);
    void visit(PrefixExprNode node);
    void visit(BinaryExprNode node);
    void visit(AssignExprNode node);

    void visit(ThisExprNode node);
    void visit(IdExprNode node); // primary->Identifier: a variable/class

    // --------- LiteralNode ----------
    void visit(IntLiteralNode node);
    void visit(StringLiteralNode node);
    void visit(BoolLiteralNode node);
    void visit(NullLiteralNode node);
}
