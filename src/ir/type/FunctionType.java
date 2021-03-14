package ir.type;

import ir.operand.IROperand;

import java.util.ArrayList;

public class FunctionType extends IRType {
    private IRType retType;
    private ArrayList<IRType> paramTypes;

    public FunctionType(IRType retType, ArrayList<IRType> paramTypes) {
        this.retType = retType;
        this.paramTypes = paramTypes;
    }

    public IRType getRetType() {
        return retType;
    }

    public ArrayList<IRType> getParamTypes() {
        return paramTypes;
    }

    @Override
    public int getBytes() {
        return retType.getBytes();
    }

    @Override
    public IROperand getDefaultValue() {
        throw new RuntimeException();
    }
}
