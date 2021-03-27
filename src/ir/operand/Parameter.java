package ir.operand;

import ir.IRFunction;
import ir.IRVisitor;
import ir.type.IRType;

public class Parameter extends IROperand {
    private String name;
    private IRFunction function;

    public Parameter(IRType type, String name) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IRFunction getFunction() {
        return function;
    }

    public void setFunction(IRFunction function) {
        this.function = function;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getType().toString() + " %" + name;
    }
}
