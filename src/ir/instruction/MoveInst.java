package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.LinkedHashSet;

public class MoveInst extends Inst {
    private Operand src;
    private Register dstReg;

    public MoveInst(Block parentBlock, Register dstReg, Operand src) {
        super(parentBlock);
        this.src = src;
        this.dstReg = dstReg;
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    public Operand getSrc() {
        return src;
    }

    public void setSrc(Operand src) {
        this.src = src;
    }

    @Override
    public void removeUse() {
        src.removeUse(this);
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        src.addUse(this);
    }

    @Override
    public LinkedHashSet<Operand> getUses() {
        LinkedHashSet<Operand> ret = new LinkedHashSet<>();
        ret.add(src);
        return ret;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (src == original) {
            src.removeUse(this);
            src = (Register) replaced;
            replaced.addUse(this);
        }
    }

    @Override
    public String toString() {
        return "move " + getDstReg().toString() + ", " + getSrc().toString();
    }

    @Override
    public Inst cloneInst(Block block) {
        throw new RuntimeException(); // won't happen
    }

    @Override
    public boolean sameMeaning(Inst q) {
        return q instanceof MoveInst && ((MoveInst) q).getSrc().equals(src);
    }
}
