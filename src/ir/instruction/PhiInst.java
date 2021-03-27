package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.operand.Register;

import java.util.ArrayList;
import java.util.Set;

public class PhiInst extends IRInst {
    // select a value depending on which block is entered from
    private Register dstReg;
    private ArrayList<IRBlock> predecessors;
    private ArrayList<IROperand> values;

    public PhiInst(IRBlock parentBlock, ArrayList<IRBlock> predecessors, ArrayList<IROperand> values, Register dstReg) {
        super(parentBlock);
        this.predecessors = predecessors;
        this.values = values;
        this.dstReg = dstReg;
        assert predecessors.size() == values.size();
    }

    public Register getDstReg() {
        return dstReg;
    }

    public ArrayList<IRBlock> getPredecessors() {
        return predecessors;
    }

    public ArrayList<IROperand> getValues() {
        return values;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
