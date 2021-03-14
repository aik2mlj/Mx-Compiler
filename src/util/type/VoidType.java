package util.type;

import ir.IRTypeTable;
import ir.type.IRType;
import util.Position;

public class VoidType extends Type {
    public VoidType() { super("void"); }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        return irTypeTable.get(this);
    }

    static public IRType getRawIRType() {
        return new ir.type.VoidType();
    }
}
