package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.PointerType;

import java.util.HashSet;

public class StoreInst extends Inst {
    private Operand value;
    private Operand pointer;

    public StoreInst(Block parentBlock, Operand value, Operand pointer) {
        super(parentBlock);
        this.value = value;
        this.pointer = pointer;
    }

    public Operand getValue() {
        assert value != null;
        return value;
    }

    public Operand getPointer() {
        return pointer;
    }

    @Override
    public void removeUse() {
        value.removeUse(this);
        pointer.removeUse(this);
    }

    @Override
    public void addUseAndDef() {
        value.addUse(this);
        pointer.addUse(this);
    }

    @Override
    public HashSet<Operand> getUses() {
        HashSet<Operand> ret = new HashSet<>();
        ret.add(value); ret.add(pointer);
        return ret;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (value == original) {
            value.removeUse(this);
            value = replaced;
            replaced.addUse(this);
        }
        if (pointer == original) {
            pointer.removeUse(this);
            pointer = replaced;
            replaced.addUse(this);
        }
    }

    @Override
    public String toString() {
        return "store " + ((PointerType) getPointer().getType()).getBaseType().toString() + " " +
                getValue().toStringWithoutType() + ", " + getPointer().toString();
    }
}
