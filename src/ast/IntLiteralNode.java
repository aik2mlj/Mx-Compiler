package ast;

import util.Position;

public class IntLiteralNode extends LiteralExprNode {
    private int value;

    public IntLiteralNode(Position pos, String text, int value) {
        super(pos, text);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<IntLiteralNode>\nvalue: " + value + "\n";
    }
}
