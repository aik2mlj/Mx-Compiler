package ir.operand;

import ir.type.IRType;

public class GlobalVar extends IROperand {
    private String name;
    private IROperand initValue;

    public GlobalVar(IRType type, String name, IROperand initValue) {
        super(type);
        this.name = name;
        this.initValue = initValue;
    }

    public String getName() {
        return name;
    }

    public IROperand getInitValue() {
        return initValue;
    }
}
