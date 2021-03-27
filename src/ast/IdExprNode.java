package ast;

import util.Position;
import util.entity.VarEntity;

public class IdExprNode extends ExprNode {
    private String identifier;
    private VarEntity varEntity;

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

    public VarEntity getVarEntity() {
        return varEntity;
    }

    public void setVarEntity(VarEntity varEntity) {
        this.varEntity = varEntity;
    }
}
