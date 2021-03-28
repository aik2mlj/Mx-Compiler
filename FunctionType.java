package ir.type;

import ir.IRVisitor;
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

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("FunctionType: " + retType.toString() + " (");
        for(int i = 0; i < paramTypes.size(); ++i) {
            ret.append(paramTypes.get(i).toString());
            if(i != paramTypes.size() - 1)
                ret.append(", ");
        }
        ret.append(")\n");
        return ret.toString();
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}

// FIXME: delete this? not needed?
