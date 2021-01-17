package ast;

import util.Position;

public class MemberExprNode extends ExprNode {
    private ExprNode prefixExpr;
    private String memberName;

    public MemberExprNode(Position pos, ExprNode prefixExpr, String memberName) {
        super(pos);
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
