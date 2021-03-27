package util.type;

import ir.IRTypeTable;
import ir.operand.IROperand;
import ir.type.IRType;
import util.Position;
import util.error.IRBuildingError;

public class VoidType extends Type {
    public VoidType() {
        super("void");
    }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        return irTypeTable.get(this);
    }

    static public IRType getRawIRType() {
        return new ir.type.VoidType();
    }

    @Override
    public IROperand getDefaultValue() {
        throw new IRBuildingError("Calling void type getDefaultValue", new Position(0, 0));
        // never called
    }
}
