package ast;

import util.Position;

public class BinaryExprNode extends ExprNode {
    public enum Operator {
        Mul, Div, Mod,
        Add, Sub,
        ShiftLeft, ShiftRight,
        Less, Greater, LessEqual, GreaterEqual,
        Equal, NotEqual,
        BitwiseAnd, BitwiseXor, BitwiseOr,
        LogicalAnd, LogicalOr
    }
    private ExprNode lhsExpr, rhsExpr;
    private Operator operator;

    public BinaryExprNode(Position pos, ExprNode lhsExpr, Operator operator, ExprNode rhsExpr) {
        super(pos);
        this.lhsExpr = lhsExpr;
        this.operator = operator;
        this.rhsExpr = rhsExpr;
    }

    public ExprNode getLhsExpr() {
        return lhsExpr;
    }

    public ExprNode getRhsExpr() {
        return rhsExpr;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<BinaryExprNode>\nlhsExpr:\n" + lhsExpr.toString() + "operator: " + operator
                + "\nrhsExpr:\n" + rhsExpr.toString();
    }
}
