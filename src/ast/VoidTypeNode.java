package ast;

import util.Position;

public class VoidTypeNode extends TypeNode {
    public VoidTypeNode(Position pos) {
        super(pos, "void");
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
