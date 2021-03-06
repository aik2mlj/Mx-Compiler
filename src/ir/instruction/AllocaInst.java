package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class AllocaInst extends Inst {
    private Register dstReg;
    private IRType type;

    public AllocaInst(Block parentBlock, Register dstReg, IRType type) {
        super(parentBlock);
        this.dstReg = dstReg;
        this.type = type;
        assert (new PointerType(type)).equals(dstReg.getType());
    }

    public IRType getType() {
        return type;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
    }

    @Override
    public void removeUse() {

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

    }

    @Override
    public String toString() {
        return getDstReg().toStringWithoutType() + " = alloca " + getType().toString();
    }

    @Override
    public Inst cloneInst(Block block) {
        var symbolTable = block.getParentFunc().getSymbolTable();
        Register dstReg = (Register) symbolTable.getClonedOperand(getDstReg());
        return new AllocaInst(block, dstReg, type);
    }

    @Override
    public boolean sameMeaning(Inst q) {
        throw new RuntimeException();
    }
}
