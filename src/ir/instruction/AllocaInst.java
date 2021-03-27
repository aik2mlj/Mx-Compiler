package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class AllocaInst extends IRInst {
    private Register dstReg;
    private IRType type;

    public AllocaInst(IRBlock parentBlock, Register dstReg, IRType type) {
        super(parentBlock);
        this.dstReg = dstReg;
        this.type = type;
        assert (new PointerType(type)).equals(dstReg.getType());
    }

    public IRType getType() {
        return type;
    }

    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
