package ir.type;

import ir.IRVisitor;
import ir.operand.Operand;

abstract public class IRType {
    abstract public int getBytes();

    abstract public Operand getDefaultValue();

    @Override
    abstract public String toString();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IRType && obj.toString().equals(this.toString());
    }
}
