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
    public boolean equals(Object obj) {
        return obj instanceof ConstInt && value == ((ConstInt) obj).value;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "i32 " + value;
    }

    @Override
    public String toStringWithoutType() {
        return String.valueOf(value);
    }
}
