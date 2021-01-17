package ast;

import util.Position;

public class VoidTypeNode extends TypeNode {
    public VoidTypeNode(Position pos, String typeName) {
        super(pos, typeName);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<VoidTypeNode>\n";
    }
}
