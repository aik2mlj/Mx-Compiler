package ast;

import util.Position;

public class SuffixExprNode extends ExprNode {
    public enum Operator {
        sufPlus, sufMinus
    }
    private ExprNode exprNode;
    private Operator operator;

    public SuffixExprNode(Position pos, ExprNode exprNode, Operator operator) {
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
        return "<SuffixExprNode>\nexprNode:\n" + exprNode.toString()
                + "operator: " + operator + "\n";
    }
}
