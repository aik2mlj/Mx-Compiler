package ir.operand;

import ir.type.IRType;

abstract public class Constant extends IROperand {
    public Constant(IRType type) {
        super(type);
    }
}
