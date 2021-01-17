package ast;

import util.Position;

abstract public class LiteralExprNode extends ExprNode {
    public LiteralExprNode(Position pos) {
        super(pos);
    }
}
