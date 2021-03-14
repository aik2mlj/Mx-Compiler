package ir.type;

import ir.operand.ConstNull;
import ir.operand.IROperand;

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
    public IROperand getDefaultValue() {
        return new ConstNull();
    }
}