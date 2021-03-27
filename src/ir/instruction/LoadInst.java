package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;

public class LoadInst extends IRInst {
    private IRType type;
    private IROperand pointer;
    private Register dstReg;

    public LoadInst(IRBlock parentBlock, IROperand pointer, Register dstReg) {
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
