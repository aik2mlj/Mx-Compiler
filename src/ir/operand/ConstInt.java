package ir.operand;

import ir.IRVisitor;
import ir.type.IRType;
import ir.type.IntType;

public class ConstInt extends Constant {
    private int value;

    public ConstInt(IntType.BitWidth bitWidth, int value) {
        super(new IntType(bitWidth));
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "i32 " + value;
    }
}
