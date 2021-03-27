package ir.instruction;

import ir.IRBlock;
import ir.IRFunction;
import ir.IRVisitor;
import ir.operand.ConstNull;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.PointerType;
import ir.type.VoidType;

import java.util.ArrayList;

public class CallInst extends IRInst {
    private Register dstReg;
    private IRFunction function;
    private ArrayList<IROperand> parameters;

    public CallInst(IRBlock parentBlock, IRFunction function, ArrayList<IROperand> parameters, Register dstReg) {
        super(parentBlock);
        this.function = function;
        this.parameters = parameters;
        this.dstReg = dstReg;
        if(dstReg != null)
            assert dstReg.getType().equals(function.getRetType());
        else assert function.getRetType() instanceof VoidType;
        assert parameters.size() == function.getParameters().size();
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i) instanceof ConstNull) {
                assert function.getParameters().get(i).getType() instanceof PointerType;
            } else {
                assert parameters.get(i).getType().equals(function.getParameters().get(i).getType());
            }
        }
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public Register getDstReg() {
        return dstReg;
    }

    public IRFunction getFunction() {
        return function;
    }

    public ArrayList<IROperand> getParameters() {
        return parameters;
    }
}
