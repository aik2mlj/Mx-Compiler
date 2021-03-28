package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.operand.Register;

import java.util.ArrayList;

public class PhiInst extends Inst {
    // select a value depending on which block is entered from
    private Register dstReg;
    private ArrayList<Block> predecessors;
    private ArrayList<IROperand> values;

    public PhiInst(Block parentBlock, ArrayList<Block> predecessors, ArrayList<IROperand> values, Register dstReg) {
        super(parentBlock);
        this.predecessors = predecessors;
        this.values = values;
        this.dstReg = dstReg;
        assert predecessors.size() == values.size();
    }

    public Register getDstReg() {
        return dstReg;
    }

    public ArrayList<Block> getPredecessors() {
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
