package ir.type;

import ir.operand.IROperand;

public class VoidType extends IRType {
    @Override
    public int getBytes() {
        return 0;
    }

    @Override
    public IROperand getDefaultValue() {
        throw new RuntimeException();
    }
}
