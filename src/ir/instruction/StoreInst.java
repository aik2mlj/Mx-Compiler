package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.IROperand;

public class StoreInst extends IRInst {
    private IROperand value;
    private IROperand pointer;

    public StoreInst(IRBlock parentBlock, IROperand value, IROperand pointer) {
        super(parentBlock);
        this.value = value;
        this.pointer = pointer;
    }

    public IROperand getValue() {
        assert value != null;
        return value;
    }

    public IROperand getPointer() {
        return pointer;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
