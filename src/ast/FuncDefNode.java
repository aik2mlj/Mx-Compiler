package ast;

import ir.Block;
import ir.Function;
import ir.IRTypeTable;
import ir.Module;
import ir.instruction.AllocaInst;
import ir.instruction.StoreInst;
import ir.operand.Parameter;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;
import util.Position;
import util.entity.FuncEntity;
import util.entity.VarEntity;
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

    public ArrayList<VarNode> getParams() {
        return params;
    }

    public BlockStmtNode getSuite() {
        return suite;
    }

    public FuncEntity getEntity(FuncEntity.EntityType entityType) {
        ArrayList<VarEntity> entityParams = new ArrayList<>();
        for (var it : params)
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
        String name;
        ArrayList<Parameter> parameters = new ArrayList<>();
        FuncEntity funcEntity;
        if (getScope().inClassScope()) {
            name = getScope().getClassType().getTypeName() + "#" + this.identifier;
            parameters.add(new Parameter(getScope().getClassType().getIRType(irTypeTable), "this")); // add "this" parameter.
            funcEntity = getScope().getFuncEntity(this.identifier);
            if (funcEntity == null) {
                funcEntity = getScope().getConstructorEntity();
            }
        } else {
            name = this.identifier;
            funcEntity = getScope().getFuncEntity(name);
        }
        ArrayList<VarEntity> paramEntities = funcEntity.getParams();
        this.params.forEach(param -> {
            parameters.add(new Parameter(astTypeTable.getType(param.getTypeNode()).getIRType(irTypeTable), param.getIdentifier()));
        });
        IRType retType = astTypeTable.getType(this.typeNode).getIRType(irTypeTable);
        Function function = new Function(module, name, retType, parameters);
        module.addFunction(function);

        // initialize this function: add alloca & store for "this" & parameters.
        function.initialize();
        Block entryBlock = function.getEntryBlock();
        //
        int offset = 0;
        if (getScope().inClassScope()) {
            offset = 1;
            var thisParam = parameters.get(0);
            Register thisAddrReg = new Register(new PointerType(thisParam.getType()), "this.addr");
            function.setThisAddr(thisAddrReg); // record this.addr in IRFunction.
            entryBlock.appendInst(new AllocaInst(entryBlock, thisAddrReg, thisParam.getType()));
            entryBlock.appendInst(new StoreInst(entryBlock, thisParam, thisAddrReg));
        }
        for (int i = 0; i < paramEntities.size(); ++i) {
            Parameter parameter = parameters.get(i + offset);
            Register addrReg = new Register(new PointerType(parameter.getType()), parameter.getName() + ".addr");
            entryBlock.appendInst(new AllocaInst(entryBlock, addrReg, parameter.getType()));
            entryBlock.appendInst(new StoreInst(entryBlock, parameter, addrReg));
            paramEntities.get(i).setAllocaAddr(addrReg);
//            System.err.println(paramEntities.get(i) + ": " + paramEntities.get(i).getName() + " " + addrReg);
        }
    }
}
