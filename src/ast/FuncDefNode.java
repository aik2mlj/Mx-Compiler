package ast;

import util.Position;
import util.entity.FuncEntity;
import util.entity.VarEntity;

import java.util.ArrayList;

public class FuncDefNode extends ASTNode {
    private TypeNode typeNode;
    private String identifier;
    private ArrayList<VarNode> params;
    private BlockStmtNode suite;

    public FuncDefNode(Position pos, TypeNode typeNode, String identifier, ArrayList<VarNode> params, BlockStmtNode suite) {
        super(pos);
        this.typeNode = typeNode;
        this.identifier = identifier;
        this.params = params;
        this.suite = suite;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ArrayList<VarNode> getParams() { return params; }

    public BlockStmtNode getSuite() { return suite; }

    public FuncEntity getEntity(FuncEntity.EntityType entityType) {
        ArrayList<VarEntity> entityParams = new ArrayList<>();
        for(var it: params)
            entityParams.add(it.getEntity(VarEntity.EntityType.Parameter));
        return new FuncEntity(identifier, getPos(), typeNode, entityParams, suite, entityType);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<FuncDefNode>\n");
        ret.append("typeNode: " + typeNode.toString());
        ret.append("identifier: " + identifier);
        ret.append("paramList:\n" + params.toString());
        ret.append("suite:\n" + suite.toString());
        return ret.toString();
    }
}
