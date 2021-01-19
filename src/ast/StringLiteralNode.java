package ast;

import util.Position;

public class StringLiteralNode extends LiteralExprNode {
    private String value;

    public StringLiteralNode(Position pos, String text, String value) {
        super(pos, text);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<StringLiteralNode>\nvalue: " + value + "\n";
    }
}
