package ast;

import com.sun.jdi.ClassType;
import parser.MxParser;

public interface ASTVisitor {
    // --------- RootNode ----------
    void visit(ProgramNode it);

    // --------- TypeNode ----------
    void visit(SingleTypeNode it);
    void visit(ArrayTypeNode it);
    void visit(VoidTypeNode it);

    // --------- DefNode ----------
    void visit(ClassDefNode it);
    void visit(FuncDefNode it);

    void visit(VarNode it); // versatile node: literally type + ID
    void visit(VarListNode it); // same

    // --------- StmtNode ----------
    void visit(BlockStmtNode it);
    void visit(VarDefStmtNode it);
    void visit(IfStmtNode it);
    void visit(ForStmtNode it);
    void visit(WhileStmtNode it);
    void visit(ReturnStmtNode it);
    void visit(BreakStmtNode it);
    void visit(ContinueStmtNode it);
    void visit(SimpleStmtNode it); // Expr | empty

    // --------- ExprNode ----------
    void visit(MemberExprNode it);
    void visit(NewExprNode it);
    void visit(SubscriptExprNode it);
    void visit(FuncCallExprNode it);
    void visit(SuffixExprNode it);
    void visit(PrefixExprNode it);
    void visit(BinaryExprNode it);
    void visit(AssignExprNode it);

    void visit(ThisExprNode it);
    void visit(IdExprNode it); // primary->Identifier: a variable/class

    // --------- LiteralNode ----------
    void visit(IntLiteralNode it);
    void visit(StringLiteralNode it);
    void visit(BoolLiteralNode it);
    void visit(NullNode it);
}
