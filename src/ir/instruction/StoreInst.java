package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;

public class StoreInst extends Inst {
    private Operand value;
    private Operand pointer;

    public StoreInst(Block parentBlock, Operand value, Operand pointer) {
        super(parentBlock);
        this.value = value;
        this.pointer = pointer;
    }

    public Operand getValue() {
        assert value != null;
        return value;
    }

    public Operand getPointer() {
        return pointer;
    }

    @Override
    public void addUseAndDef() {
        value.addUse(this);
        pointer.addUse(this);
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
