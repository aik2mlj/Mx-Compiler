package ir.operand;

import ir.IRVisitor;
import ir.type.IRType;
import ir.type.PointerType;

public class GlobalVar extends Operand {
    private String name;
    private Operand initValue;

    public GlobalVar(IRType type, String name, Operand initValue) {
        super(type);
        this.name = name;
        this.initValue = initValue;
        assert type instanceof PointerType;
        assert ((PointerType) type).getBaseType().equals(initValue.getType());
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public Operand getInitValue() {
        return initValue;
    }

    public void setInitValue(Operand initValue) {
        this.initValue = initValue;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getType().toString() +  " @" + name;
    }

    @Override
    public String toStringWithoutType() {
        return "@" + name;
    }
}
