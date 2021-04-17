package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class LoadInst extends Inst {
    private IRType type;
    private Operand pointer;
    private Register dstReg;

    public LoadInst(Block parentBlock, Operand pointer, Register dstReg) {
        super(parentBlock);
        assert pointer.getType() instanceof PointerType;
        assert ((PointerType) pointer.getType()).getBaseType().equals(dstReg.getType());
        this.type = dstReg.getType();
        this.pointer = pointer;
        this.dstReg = dstReg;
    }

    public IRType getType() {
        return type;
    }

    public Operand getPointer() {
        return pointer;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        pointer.addUse(this);
    }

    @Override
    protected void removeUse() {
        pointer.removeUse(this);
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (pointer == original) {
            pointer.removeUse(this);
            pointer = replaced;
            replaced.addUse(this);
        }
    }
}
