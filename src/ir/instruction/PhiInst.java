package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.ArrayList;
import java.util.LinkedHashSet;

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

    public void addOrgin(Operand value, Block fromBlock) {
        predecessors.add(fromBlock);
        values.add(value);
        value.addUse(this);
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
    public LinkedHashSet<Operand> getUses() {
        LinkedHashSet<Operand> ret = new LinkedHashSet<>(values);
        return ret;
    }

    @Override
    public void removeUse() {
        for (Operand value : values) {
            value.removeUse(this);
        }
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

    @Override
    public void replaceUse(Register original, Operand replaced) {
        for (int i = 0; i < values.size(); ++i)
            if (values.get(i) == original) {
                values.get(i).removeUse(this);
                values.set(i, replaced);
                replaced.addUse(this);
            }
    }

    public void replaceBlock(Block original, Block replaced) {
        for (int i = 0; i < predecessors.size(); ++i)
            if (predecessors.get(i) == original)
                predecessors.set(i, replaced);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(getDstReg().toStringWithoutType() + " = phi " + getDstReg().getType().toString() + " ");
        for (int i = 0; i < getPredecessors().size(); ++i) {
            ret.append("[ " + getValues().get(i).toStringWithoutType() + ", " + getPredecessors().get(i).toString() + " ]");
            if (i != getPredecessors().size() - 1)
                ret.append(", ");
        }
        return ret.toString();
    }

    @Override
    public Inst cloneInst(Block block) {
        var symbolTable = block.getParentFunc().getSymbolTable();
        Register dstReg = (Register) symbolTable.getClonedOperand(getDstReg());
        var preds = new ArrayList<Block>();
        this.predecessors.forEach(pred -> preds.add(symbolTable.getClonedBlock(pred)));
        var values = new ArrayList<Operand>();
        this.values.forEach(value -> values.add(symbolTable.getClonedOperand(value)));
        return new PhiInst(block, preds, values, dstReg);
    }

    @Override
    public boolean sameMeaning(Inst q) {
        return false; // TODO: consider phi as well.
    }

    public boolean clean() {
        boolean ret = false;
        Operand sameOperand = null;
        boolean same = true;
        for (int i = 0; i < predecessors.size(); ) {
            var pred = predecessors.get(i);
            if (!getParentBlock().getPredecessors().contains(pred)) {
                predecessors.remove(i);
                values.get(i).removeUse(this);
                values.remove(i);
                ret = true;
                continue;
            }
            ++i;
        }
        for (Operand value : values) {
            if (sameOperand != null) {
                if (!sameOperand.equals(value)) {
                    same = false;
                    break;
                }
            } else sameOperand = value;
        }
        if (same) {
            ret = true;
            dstReg.replaceAllUseWith(sameOperand);
            this.removeFromBlock();
        }
        return ret;
    }
}
