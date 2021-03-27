package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class BitcastToInst extends IRInst {
    private Register dstReg;
    private IROperand src;
    private IRType dstType;

    public BitcastToInst(IRBlock parentBlock, IROperand src, Register dstReg) {
        super(parentBlock);
        this.dstReg = dstReg;
        this.src = src;
        this.dstType = dstReg.getType();

        assert dstType instanceof PointerType;
        assert src.getType() instanceof PointerType;
    }

    public Register getDstReg() {
        return dstReg;
    }

    public IROperand getSrc() {
        return src;
    }

    public IRType getDstType() {
        return dstType;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
