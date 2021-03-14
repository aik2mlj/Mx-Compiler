package ir.operand;

import ir.type.IRType;
import ir.type.PointerType;
import ir.type.VoidType;

public class ConstNull extends Constant {
    public ConstNull() {
        super(new PointerType(new VoidType()));
    }
}
