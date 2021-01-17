package ast;

import util.Position;

abstract public class ASTNode {
    private Position pos;

    public ASTNode(Position pos) {
        this.pos = pos;
    }

    public Position getPos() {
        return pos;
    }

    abstract public void accept(ASTVisitor visitor);

    abstract public String toString();
}
