package ast;

import util.Position;

public class SubscriptExprNode extends ExprNode {
    private ExprNode nameExpr, indexExpr;
    private int dimension;

    public SubscriptExprNode(Position pos, String text, ExprNode nameExpr, ExprNode indexExpr) {
        super(pos,text);
        this.nameExpr = nameExpr;
        this.indexExpr = indexExpr;
        if(nameExpr instanceof SubscriptExprNode)
            dimension = ((SubscriptExprNode) nameExpr).dimension + 1;
        else
            dimension = 1;
    }

    public ExprNode getNameExpr() {
        return nameExpr;
    }

    public ExprNode getIndexExpr() {
        return indexExpr;
    }

    public int getDimension() {
        return dimension;
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
        return "<SubscriptExprNode>\nnameNode:\n" + nameExpr.toString() + "indexNode:\n" + indexExpr.toString()
                 + "dimension: " + dimension;
    }
}
