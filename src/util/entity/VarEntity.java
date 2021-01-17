package util.entity;

import ast.ExprNode;
import ast.SingleTypeNode;
import ast.TypeNode;
import util.Position;

public class VarEntity extends Entity {
    public enum EntityType {
        global, local, parameter, member
    }
    private TypeNode typeNode;
    private ExprNode initExpr;
    private EntityType entityType;

    public VarEntity(String name, Position pos, TypeNode typeNode, ExprNode initExpr, EntityType entityType) {
        super(name, pos);
        this.typeNode = typeNode;
        this.initExpr = initExpr;
        this.entityType = entityType;
    }

    public static VarEntity newBuiltInParam(String typeName, String name) {
        return new VarEntity(name, new Position(0, 0), new SingleTypeNode(new Position(0, 0), typeName),
                null, EntityType.parameter);
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
