package ast;

import ir.IRFunction;
import ir.IRTypeTable;
import ir.Module;
import ir.operand.Parameter;
import ir.type.IRType;
import util.Position;
import util.entity.FuncEntity;
import util.entity.VarEntity;
import util.type.ClassType;
import util.type.Type;
import util.type.TypeTable;

import java.util.ArrayList;

public class FuncDefNode extends ProgramUnitNode {
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

    public boolean hasTypeNode() {
        return typeNode != null;
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

    public void addFunctionToModule(Module module, TypeTable astTypeTable, IRTypeTable irTypeTable) {
        String name = this.identifier;
        IRType retType = astTypeTable.getType(this.typeNode).getIRType(irTypeTable);
        ArrayList<Parameter> parameters = new ArrayList<>();
        this.params.forEach(param -> {
            parameters.add(new Parameter(astTypeTable.getType(param.getTypeNode()).getIRType(irTypeTable), param.getIdentifier()));
        });
        IRFunction irFunction = new IRFunction(module, name, retType, parameters);
    }

    public void addMethodToModule(Module module, ClassType classType, TypeTable astTypeTable, IRTypeTable irTypeTable) {
        String name = classType.getTypeName() + "_" + this.identifier;
        IRType retType = astTypeTable.getType(this.typeNode).getIRType(irTypeTable);
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter(irTypeTable.get(classType), "this")); // add "this" parameter.
        this.params.forEach(param -> {
            parameters.add(new Parameter(astTypeTable.getType(param.getTypeNode()).getIRType(irTypeTable), param.getIdentifier()));
        });
        IRFunction irFunction = new IRFunction(module, name, retType, parameters);
        module.addFunction(irFunction);
    }
}
