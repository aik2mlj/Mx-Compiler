package ir.type;

import ir.IRVisitor;
import ir.operand.ConstBool;
import ir.operand.ConstInt;
import ir.operand.Operand;

public class IntType extends IRType {
    public enum BitWidth {
        i1, i8, i32
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
            case i1, i8 -> 1;
            case i32 -> 4;
        };
    }

    @Override
    public Operand getDefaultValue() {
        if(bitWidth == BitWidth.i1)
            return new ConstBool(false);
        else return new ConstInt(bitWidth, 0);
    }

    @Override
    public String toString() {
        return bitWidth.name();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
