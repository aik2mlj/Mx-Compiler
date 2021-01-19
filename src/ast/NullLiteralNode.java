package ast;

import util.Position;

public class NullLiteralNode extends LiteralExprNode {
    public NullLiteralNode(Position pos, String text) {
        super(pos, text);
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
