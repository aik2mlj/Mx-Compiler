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
import java.util.HashSet;

public class CallInst extends Inst {
    private Register dstReg;
    private Function function;
    private ArrayList<Operand> parameters;

    public CallInst(Block parentBlock, Function function, ArrayList<Operand> parameters, Register dstReg) {
        super(parentBlock);
        this.function = function;
        this.parameters = parameters;
        this.dstReg = dstReg;
        if (dstReg != null)
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
    public HashSet<Operand> getUses() {
        HashSet<Operand> ret = new HashSet<>(parameters);
        return ret;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        for (int i = 0; i < parameters.size(); ++i)
            if (parameters.get(i) == original) {
                parameters.get(i).removeUse(this);
                parameters.set(i, replaced);
                replaced.addUse(this);
            }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder((getDstReg() != null? getDstReg().toStringWithoutType() + " = " : "") + "call " +
                getFunction().getRetType().toString() + " @" + getFunction().getName() + "(");
        for (int i = 0; i < getParameters().size(); ++i) {
            ret.append(getFunction().getParameters().get(i).getType().toString() + " " + getParameters().get(i).toStringWithoutType());
            if (i != getParameters().size() - 1)
                ret.append(", ");
        }
        ret.append(")");
        return ret.toString();
    }

    @Override
    public void removeUse() {
        for (int i = 0; i < parameters.size(); ++i) {
            parameters.get(i).removeUse(this);
        }
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
