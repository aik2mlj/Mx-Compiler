package ast;

import util.Position;
import util.Scope;

abstract public class ASTNode {
    private Position pos;
    private Scope scope;

    public ASTNode(Position pos) {
        this.pos = pos;
        this.scope = null;
    }

    public Position getPos() {
        return pos;
    }

    abstract public void accept(ASTVisitor visitor);

    abstract public String toString();

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        // used for LinkedHashMap comparing
        if(obj instanceof ASTNode)
            return toString().equals(obj.toString());
        else return false;
    }
}
