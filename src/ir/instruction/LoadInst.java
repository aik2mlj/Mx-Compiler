package ir.instruction;

import ir.IRBlock;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.IRType;

public class LoadInst extends IRInst {
    private IRType type;
    private IROperand pointer;
    private Register dstReg;

    public LoadInst(IRBlock parentBlock, IRType type, IROperand pointer, Register dstReg) {
        super(parentBlock);
        this.type = type;
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
}
