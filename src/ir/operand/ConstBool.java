package ir.operand;

import ir.type.IRType;
import ir.type.IntType;

public class ConstBool extends Constant {
    public boolean value;

    public ConstBool(boolean value) {
        super(new IntType(IntType.BitWidth.int1));
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
