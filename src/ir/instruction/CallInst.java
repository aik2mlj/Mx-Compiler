package ir.instruction;

import ir.Block;
import ir.Function;
import ir.IRVisitor;
import ir.operand.ConstNull;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.PointerType;
import ir.type.VoidType;

import java.util.ArrayList;

public class CallInst extends Inst {
    private Register dstReg;
    private Function function;
    private ArrayList<Operand> parameters;

    public CallInst(Block parentBlock, Function function, ArrayList<Operand> parameters, Register dstReg) {
        super(parentBlock);
        this.function = function;
        this.parameters = parameters;
        this.dstReg = dstReg;
        if(dstReg != null)
            assert dstReg.getType().equals(function.getRetType());
        else assert function.getRetType() instanceof VoidType;
        assert parameters.size() == function.getParameters().size();
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i) instanceof ConstNull) {
                assert function.getParameters().get(i).getType() instanceof PointerType;
            } else {
                assert parameters.get(i).getType().equals(function.getParameters().get(i).getType());
            }
        }
    }

    @Override
    public void addUseAndDef() {
        if (dstReg != null)
            dstReg.setDefInst(this);
        parameters.forEach(param -> param.addUse(this));
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    public Function getFunction() {
        return function;
    }

    public ArrayList<Operand> getParameters() {
        return parameters;
    }
}
