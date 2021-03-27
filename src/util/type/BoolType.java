package util.type;

import ir.IRTypeTable;
import ir.operand.ConstBool;
import ir.operand.IROperand;
import ir.type.IRType;

public class BoolType extends BasicType {
    public BoolType() {
        super("bool");
    }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        return irTypeTable.get(this);
    }

    static public IRType getRawIRType() {
        return new ir.type.IntType(ir.type.IntType.BitWidth.i1);
    }

    @Override
    public IROperand getDefaultValue() {
        return new ConstBool(false);
    }
}
