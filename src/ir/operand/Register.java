package ir.operand;

import ir.IRVisitor;
import ir.instruction.IRInst;
import ir.type.IRType;

public class Register extends IROperand {
    private String name;
    private IRInst defInst;

    public Register(IRType type, String name) {
        super(type);
        this.name = name;
    }

    public void setDefInst(IRInst defInst) { // TODO: where to set DefInst?
        this.defInst = defInst;
    }

    public IRInst getDefInst() {
        return defInst;
    }

    public String getName() {
        return name;
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
