package ir.operand;

import ir.type.ArrayType;
import ir.type.IRType;
import ir.type.IntType;
import ir.type.PointerType;

public class ConstString extends Constant {
    private String value;

    public ConstString(String value) {
        super(new PointerType(new ArrayType(value.length(), new IntType(IntType.BitWidth.int8))));
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
