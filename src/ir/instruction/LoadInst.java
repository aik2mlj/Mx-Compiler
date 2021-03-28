package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class LoadInst extends Inst {
    private IRType type;
    private IROperand pointer;
    private Register dstReg;

    public LoadInst(Block parentBlock, IROperand pointer, Register dstReg) {
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

    public IROperand getPointer() {
        return pointer;
    }

    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
