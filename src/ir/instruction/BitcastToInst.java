package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class BitcastToInst extends Inst {
    private Register dstReg;
    private Operand src;
    private IRType dstType;

    public BitcastToInst(Block parentBlock, Operand src, Register dstReg) {
        super(parentBlock);
        this.dstReg = dstReg;
        this.src = src;
        this.dstType = dstReg.getType();

        assert dstType instanceof PointerType;
        assert src.getType() instanceof PointerType;
    }

    @Override
    protected void removeUse() {
        src.removeUse(this);
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        src.addUse(this);
    }

    public Operand getSrc() {
        return src;
    }

    public IRType getDstType() {
        return dstType;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (src == original) {
            src.removeUse(this);
            src = replaced;
            replaced.addUse(this);
        }
    }
}
