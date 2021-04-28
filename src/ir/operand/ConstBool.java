package ir.operand;

import ir.IRVisitor;
import ir.type.IntType;

public class ConstBool extends Constant {
    private boolean value;

    public ConstBool(boolean value) {
        super(new IntType(IntType.BitWidth.i1));
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConstBool && value == ((ConstBool) obj).getValue();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "i1 " + value;
    }

    @Override
    public String toStringWithoutType() {
        return String.valueOf(value);
    }
}
