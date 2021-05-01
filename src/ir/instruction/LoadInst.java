package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

import java.util.LinkedHashSet;

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
    public LinkedHashSet<Operand> getUses() {
        LinkedHashSet<Operand> ret = new LinkedHashSet<>();
        ret.add(pointer);
        return ret;
    }

    @Override
    public void removeUse() {
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

    @Override
    public String toString() {
        return getDstReg().toStringWithoutType() + " = load " + getType().toString() + ", " + getPointer().toString();
    }

    @Override
    public Inst cloneInst(Block block) {
        var symbolTable = block.getParentFunc().getSymbolTable();
        Register dstReg = (Register) symbolTable.getClonedOperand(getDstReg());
        var pointer = symbolTable.getClonedOperand(this.pointer);
        return new LoadInst(block, pointer, dstReg);
    }

    @Override
    public boolean sameMeaning(Inst q) {
        return false;
    }
}
