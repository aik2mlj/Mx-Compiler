package ast;

import util.Position;
import util.entity.VarEntity;

public class VarNode extends ProgramUnitNode {
    // typeNode identifier = initExpr;
    // appear in VarListNode, ClassDefNode(ArrayList), FuncDefNode(ArrayList)
    private TypeNode typeNode;
    private String identifier;
    private ExprNode initExpr;

    public VarNode(Position pos, TypeNode typeNode, String identifier, ExprNode initExpr) {
        super(pos);
        this.typeNode = typeNode;
        this.identifier = identifier;
        this.initExpr = initExpr;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean hasInitExpr() {
        return initExpr != null;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    public void setInitExpr(ExprNode initExpr) {
        this.initExpr = initExpr;
    }

    public void setTypeNode(TypeNode typeNode) {
        this.typeNode = typeNode;
    }

    public VarEntity getEntity(VarEntity.EntityType entityType) {
        return new VarEntity(identifier, getPos(), typeNode, initExpr, entityType);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<VarNode>\ntypeNode: " + typeNode.toString() + "identifier: " + identifier +
                (this.hasInitExpr()? "\ninitExpr:\n" + initExpr.toString(): "") + "\n";
    }
}
