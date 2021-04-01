package ir.operand;

import ir.type.IRType;

abstract public class Constant extends Operand {
    public Constant(IRType type) {
        super(type);
    }
}
