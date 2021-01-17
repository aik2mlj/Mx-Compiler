package ast;

import util.Position;

public class ArrayTypeNode extends TypeNode {
    private TypeNode baseTypeNode;
    private int dimension;

    public ArrayTypeNode(Position pos, TypeNode baseTypeNode, int dimension) {
        super(pos, baseTypeNode.getTypeName());
        this.baseTypeNode = baseTypeNode;
        this.dimension = dimension;
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
