package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.IRType;

import java.util.HashSet;

public class RetInst extends TerminalInst {
    private IRType retType;
    private Operand retValue;

    public RetInst(Block parentBlock, IRType retType, Operand retValue) {
        super(parentBlock);
        this.retType = retType;
        this.retValue = retValue;
    }

    public IRType getRetType() {
        return retType;
    }

    public Operand getRetValue() {
        return retValue;
    }

    @Override
    public void removeUse() {
        if (retValue != null)
            retValue.removeUse(this);
    }

    @Override
    public void addUseAndDef() {
        if (retValue != null)
            retValue.addUse(this);
    }

    @Override
    public HashSet<Operand> getUses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(retValue);
        return ret;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (retValue == original) {
            retValue.removeUse(this);
            retValue = replaced;
            replaced.addUse(this);
        }
    }

    @Override
    public String toString() {
        return "ret " + getRetType().toString() + (getRetValue() != null ? " " + getRetValue().toStringWithoutType() : "");
    }
}
