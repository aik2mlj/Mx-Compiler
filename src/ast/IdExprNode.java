package ast;

import util.Position;

public class IdExprNode extends ExprNode {
    private String identifier;

    public IdExprNode(Position pos, String text, String identifier) {
        super(pos, text);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<IdExprNode>\nidentifier: " + identifier + "\n";
    }
}
