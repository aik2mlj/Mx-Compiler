package ast;

import util.Position;

abstract public class TypeNode extends ASTNode {
    protected String typeName;

    public TypeNode(Position pos, String typeName) {
        super(pos);
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
