package ir.operand;

import ir.IRVisitor;
import ir.type.IRType;
import ir.type.PointerType;

public class GlobalVar extends IROperand {
    private String name;
    private IROperand initValue;

    public GlobalVar(IRType type, String name, IROperand initValue) {
        super(type);
        this.name = name;
        this.initValue = initValue;
        assert type instanceof PointerType;
        assert ((PointerType) type).getBaseType().equals(initValue.getType());
    }

    public String getName() {
        return name;
    }

    public IROperand getInitValue() {
        return initValue;
    }

    public void setInitValue(IROperand initValue) {
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
}
