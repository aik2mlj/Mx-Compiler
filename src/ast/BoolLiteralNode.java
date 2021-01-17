package ast;

import util.Position;

public class BoolLiteralNode extends LiteralExprNode {
    private boolean value;

    public BoolLiteralNode(Position pos, boolean value) {
        super(pos);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<BoolLiteralNode>\nvalue: " + value + "\n";
    }
}
