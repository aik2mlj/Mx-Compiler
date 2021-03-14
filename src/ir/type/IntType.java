package ir.type;

import ir.operand.ConstBool;
import ir.operand.ConstInt;
import ir.operand.IROperand;

public class IntType extends IRType {
    public enum BitWidth {
        int1, int8, int32
    }

    private BitWidth bitWidth;

    public IntType(BitWidth bitWidth) {
        this.bitWidth = bitWidth;
    }

    public BitWidth getBitWidth() {
        return bitWidth;
    }

    @Override
    public int getBytes() {
        return switch (bitWidth) {
            case int1, int8 -> 1;
            case int32 -> 4;
        };
    }

    @Override
    public IROperand getDefaultValue() {
        if(bitWidth == BitWidth.int1)
            return new ConstBool(false);
        else return new ConstInt(bitWidth, 0);
    }
}
