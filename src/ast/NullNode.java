package ast;

import util.Position;

public class NullNode extends LiteralExprNode {
    public NullNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<NullNode>\n";
    }
}
