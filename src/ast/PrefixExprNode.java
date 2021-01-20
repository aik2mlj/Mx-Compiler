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

    public PrefixExprNode(Position pos, String text, Operator operator, ExprNode exprNode) {
        super(pos, text);
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
    public boolean isAssignable() {
        // ++a / --a is assignable!
        if(operator == Operator.PrePlus || operator == Operator.PreMinus)
            return true;
        else return false;
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
