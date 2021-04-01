package util.type;

import ir.IRTypeTable;
import ir.operand.ConstNull;
import ir.operand.Operand;
import ir.type.IRType;
import ir.type.PointerType;
import ir.type.VoidType;

public class NullType extends Type {
    public NullType() {
        super("null");
    }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        return getRawIRType();
    }

    static public IRType getRawIRType() {
        return new PointerType(new VoidType());
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }
}
