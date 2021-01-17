package ast;

import util.Position;

public class SubscriptExprNode extends ExprNode {
    private ExprNode nameNode, indexNode;
    private int dimension;

    public SubscriptExprNode(Position pos, ExprNode nameNode, ExprNode indexNode) {
        super(pos);
        this.nameNode = nameNode;
        this.indexNode = indexNode;
        if(nameNode instanceof SubscriptExprNode)
            dimension = ((SubscriptExprNode) nameNode).dimension + 1;
        else
            dimension = 1;
    }

    public ExprNode getNameNode() {
        return nameNode;
    }

    public ExprNode getIndexNode() {
        return indexNode;
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
        return "<SubscriptExprNode>\nnameNode:\n" + nameNode.toString() + "indexNode:\n" + indexNode.toString()
                 + "dimension: " + dimension;
    }
}
