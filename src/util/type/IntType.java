package util.type;

import ir.IRTypeTable;
import ir.operand.ConstInt;
import ir.operand.Operand;
import ir.type.IRType;

public class IntType extends BasicType {
    public IntType() {
        super("int");
    }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        return irTypeTable.get(this);
    }

    static public IRType getRawIRType() {
        return new ir.type.IntType(ir.type.IntType.BitWidth.i32);
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstInt(ir.type.IntType.BitWidth.i32, 0);
    }
}
