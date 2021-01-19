package ast;

import util.Position;

public class AssignExprNode extends ExprNode {
    private ExprNode lhsExpr, rhsExpr;

    public AssignExprNode(Position pos, String text, ExprNode lhsExpr, ExprNode rhsExpr) {
        super(pos, text);
        this.lhsExpr = lhsExpr;
        this.rhsExpr = rhsExpr;
    }

    public ExprNode getLhsExpr() {
        return lhsExpr;
    }

    public ExprNode getRhsExpr() {
        return rhsExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<AssignExprNode>\nlhsExpr:\n" + lhsExpr.toString() + "rhsExpr:\n" + rhsExpr.toString();
    }
}
