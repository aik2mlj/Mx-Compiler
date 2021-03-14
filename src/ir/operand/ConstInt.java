package ir.operand;

import ir.type.IRType;
import ir.type.IntType;

public class ConstInt extends Constant {
    private long value;

    public ConstInt(IntType.BitWidth bitWidth, long value) {
        super(new IntType(bitWidth));
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
