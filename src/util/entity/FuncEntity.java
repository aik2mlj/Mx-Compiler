package util.entity;

import ast.BlockStmtNode;
import ast.TypeNode;
import util.Position;

import java.util.ArrayList;

public class FuncEntity extends Entity {
    public enum EntityType {
        Function, Constructor, Method
    }
    private TypeNode typeNode;
    private ArrayList<VarEntity> params;
    private BlockStmtNode suite;
    private EntityType entityType;

    public FuncEntity(String name, Position pos, TypeNode typeNode, ArrayList<VarEntity> params,
                      BlockStmtNode suite, EntityType entityType) {
        super(name, pos);
        this.typeNode = typeNode;
        this.params = params;
        this.suite = suite;
        this.entityType = entityType;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public ArrayList<VarEntity> getParams() {
        return params;
    }

    public BlockStmtNode getSuite() {
        return suite;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
