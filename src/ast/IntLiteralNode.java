package ast;

import util.Position;

public class IntLiteralNode extends LiteralExprNode {
    private int value;

    public IntLiteralNode(Position pos, int value) {
        super(pos);
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
