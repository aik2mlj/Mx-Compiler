package ir.operand;

import ir.IRVisitor;
import ir.type.ArrayType;
import ir.type.IntType;
import ir.type.PointerType;

public class ConstString extends Constant {
    private String value;

    public ConstString(String value) {
        super(new PointerType(new ArrayType(value.length(), new IntType(IntType.BitWidth.i8))));
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "c\"" + value + "\"";
    }
}
