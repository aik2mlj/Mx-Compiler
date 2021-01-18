package ast;

import util.Position;
import util.type.Type;

abstract public class ExprNode extends ASTNode {
    // type is specified at semantic check stage
    private Type type;

    public ExprNode(Position pos) {
        super(pos);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isAssignable() {
        return false;
    }
}
