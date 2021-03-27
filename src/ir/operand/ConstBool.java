package ir.operand;

import ir.IRVisitor;
import ir.type.IntType;

public class ConstBool extends Constant {
    public boolean value;

    public ConstBool(boolean value) {
        super(new IntType(IntType.BitWidth.i1));
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "i1 " + value;
    }
}
