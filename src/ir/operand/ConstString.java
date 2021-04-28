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
    public boolean equals(Object obj) {
        return obj instanceof ConstString && value.equals(((ConstString) obj).value);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        String tmp = value;
        tmp = tmp.replace("\\", "\\5C");
        tmp = tmp.replace("\n", "\\0A");
        tmp = tmp.replace("\"", "\\22");
        tmp = tmp.replace("\0", "\\00");
        return "c\"" + tmp + "\"";
    }

    @Override
    public String toStringWithoutType() {
        return toString();
    }
}
