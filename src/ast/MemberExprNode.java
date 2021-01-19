package ast;

import util.Position;

public class MemberExprNode extends ExprNode {
    private ExprNode prefixExpr;
    private String memberName;
    // notice that memberName here can also be a name of method

    public MemberExprNode(Position pos, String text, ExprNode prefixExpr, String memberName) {
        super(pos, text);
        this.prefixExpr = prefixExpr;
        this.memberName = memberName;
    }

    public ExprNode getPrefixExpr() {
        return prefixExpr;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<MemberExprNode>\nprefixExpr:\n" + prefixExpr.toString() + "memberName: " + memberName + "\n";
    }
}
