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

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // used for HashMap comparing
        if(obj instanceof ASTNode)
            return toString().equals(obj.toString());
        else return false;
    }
}
