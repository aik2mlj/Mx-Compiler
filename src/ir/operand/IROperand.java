package ir.operand;

import ir.type.IRType;

abstract public class IROperand {
    private IRType type;

    public IROperand(IRType type) {
        this.type = type;
    }

    public IRType getType() {
        return type;
    }
}
