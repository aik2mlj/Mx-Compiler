package ast;

import util.Position;

public class SingleTypeNode extends TypeNode {
    // identifier | int | bool | string
    public SingleTypeNode(Position pos, String typeName) {
        super(pos, typeName);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<SingleTypeNode>\n" + "typeName: " + typeName + "\n";
    }
}
