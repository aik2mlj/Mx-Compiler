package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.HashSet;

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
    public HashSet<Operand> getUses() {
        HashSet<Operand> ret = new HashSet<>();
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
}
