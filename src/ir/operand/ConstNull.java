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
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "null";
    }
}
