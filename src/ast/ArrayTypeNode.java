package ast;

import util.Position;

public class ArrayTypeNode extends TypeNode {
    private TypeNode baseTypeNode;
    private int dimension;

    public ArrayTypeNode(Position pos, TypeNode preTypeNode) {
        super(pos, preTypeNode.getTypeName());
        if(preTypeNode instanceof ArrayTypeNode) {
            this.baseTypeNode = ((ArrayTypeNode) preTypeNode).baseTypeNode;
            this.dimension = ((ArrayTypeNode) preTypeNode).dimension + 1;
        } else {
            this.baseTypeNode = preTypeNode;
            this.dimension = 1;
        }
    }

    public TypeNode getBaseTypeNode() {
        return baseTypeNode;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<ArrayTypeNode>\nbaseTypeNode" + baseTypeNode.toString() + "dimension: " + dimension + "\n";
    }
}
