package ir.type;

import ir.IRVisitor;
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

    @Override
    public String toString() {
        return "[" + size + " x " + baseType.toString() + "]";
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
