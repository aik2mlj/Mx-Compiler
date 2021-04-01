package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.ArrayList;

public class PhiInst extends Inst {
    // select a value depending on which block is entered from
    private Register dstReg;
    private ArrayList<Block> predecessors;
    private ArrayList<Operand> values;

    public PhiInst(Block parentBlock, ArrayList<Block> predecessors, ArrayList<Operand> values, Register dstReg) {
        super(parentBlock);
        this.predecessors = predecessors;
        this.values = values;
        this.dstReg = dstReg;
        assert predecessors.size() == values.size();
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        for (Operand value : values) {
            value.addUse(this);
        }
        //TODO
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    public ArrayList<Block> getPredecessors() {
        return predecessors;
    }

    public ArrayList<Operand> getValues() {
        return values;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
