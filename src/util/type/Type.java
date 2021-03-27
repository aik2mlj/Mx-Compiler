package util.type;

import ir.IRTypeTable;
import ir.operand.IROperand;
import ir.type.IRType;

abstract public class Type {
    // name: ID | int | bool | string; members: if this is a class, class members.
    private String typeName;

    public Type(String typeName) {
        this.typeName = typeName;
    }

    public boolean isTerminalType() {
        return typeName.equals("int") || typeName.equals("bool") || typeName.equals("string") || typeName.equals("void");
    }

    public static boolean canAssign(Type lType, Type rType) {
        if(lType instanceof ArrayType || lType instanceof ClassType) {
            if(rType instanceof NullType)
                return true;
            else return lType.equals(rType);
        } else return lType.equals(rType);
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Type)
            return getTypeName().equals(((Type) obj).getTypeName());
        else return false;
    }

    @Override
    public int hashCode() {
        return typeName.hashCode();
    }

    abstract public IRType getIRType(IRTypeTable irTypeTable);

    abstract public IROperand getDefaultValue();
}
