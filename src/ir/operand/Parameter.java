package ir.operand;

import ir.Function;
import ir.IRVisitor;
import ir.type.IRType;

public class Parameter extends Operand {
    private String name;
    private Function function;

    public Parameter(IRType type, String name) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
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
