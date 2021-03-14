package ir.type;

import ir.operand.IROperand;

abstract public class IRType {
    abstract public int getBytes();

    abstract public IROperand getDefaultValue();
}
