package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Register;

public class MoveInst extends Inst {
    private Register srcReg;
    private Register dstReg;

    public MoveInst(Block parentBlock, Register srcReg, Register dstReg) {
        super(parentBlock);
        this.srcReg = srcReg;
        this.dstReg = dstReg;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        srcReg.addUse(this);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
