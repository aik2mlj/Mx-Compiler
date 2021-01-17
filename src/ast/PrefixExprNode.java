package ast;

import util.Position;

public class PrefixExprNode extends ExprNode {
    public enum Operator {
        SignPos, SignNeg,
        PrePlus, PreMinus,
        BitwiseNot, LogicalNot
    }
    private ExprNode exprNode;
    private Operator operator;

    public PrefixExprNode(Position pos, Operator operator, ExprNode exprNode) {
        super(pos);
        this.exprNode = exprNode;
        this.operator = operator;
    }

    public ExprNode getExprNode() {
        return exprNode;
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
        return "<PrefixExprNode>\noperator:\n" + operator
                + "\nexprNode: " + exprNode.toString();
    }
}
