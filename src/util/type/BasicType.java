package util.type;

import ir.type.IRType;

abstract public class BasicType extends Type {
    // int | bool | string
    public BasicType(String typeName) {
        super(typeName);
    }
}
