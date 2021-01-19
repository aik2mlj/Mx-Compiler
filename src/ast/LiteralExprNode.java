package ast;

import util.Position;

abstract public class LiteralExprNode extends ExprNode {
    public LiteralExprNode(Position pos, String text) {
        super(pos, text);
    }
}
