package ir.type;

import ir.IRVisitor;
import ir.operand.ConstNull;
import ir.operand.Operand;

public class PointerType extends IRType {
    private IRType baseType;

    public PointerType(IRType baseType) {
        this.baseType = baseType;
    }

    public IRType getBaseType() {
        return baseType;
    }

    @Override
    public int getBytes() {
        return 4;
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }

    @Override
    public String toString() {
        return baseType.toString() + "*";
    }
}
