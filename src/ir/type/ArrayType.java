package ir.type;

import ir.operand.IROperand;

public class ArrayType extends IRType {
    private int size;
    private IRType baseType;

    public ArrayType(int size, IRType baseType) {
        this.size = size;
        this.baseType = baseType;
    }

    public int getSize() {
        return size;
    }

    public IRType getBaseType() {
        return baseType;
    }

    @Override
    public int getBytes() {
        return size * baseType.getBytes();
    }

    @Override
    public IROperand getDefaultValue() {
        throw new RuntimeException();
    }
}
