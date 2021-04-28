package ir.operand;

import ir.IRVisitor;
import ir.type.IRType;
import ir.type.PointerType;
import ir.type.VoidType;

public class ConstNull extends Constant {
    public ConstNull() {
        super(new PointerType(new VoidType()));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConstNull;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toStringWithoutType() {
        return toString();
    }
}
