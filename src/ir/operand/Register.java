package ir.operand;

import ir.instruction.IRInst;
import ir.type.IRType;

public class Register extends IROperand {
    private String name;
    private IRInst defInst;

    public Register(IRType type, String name) {
        super(type);
        this.name = name;
    }

    public void setDefInst(IRInst defInst) {
        this.defInst = defInst;
    }

    public IRInst getDefInst() {
        return defInst;
    }

    public String getName() {
        return name;
    }
}
