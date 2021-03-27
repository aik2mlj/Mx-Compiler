package ir.type;

import ir.IRVisitor;
import ir.operand.IROperand;

abstract public class IRType {
    abstract public int getBytes();

    abstract public IROperand getDefaultValue();

    abstract void accept(IRVisitor visitor);

    @Override
    abstract public String toString();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRType && obj.toString().equals(this.toString());
    }
}
