package ir;

import ir.instruction.BrInst;
import ir.instruction.IRInst;
import ir.instruction.TerminalInst;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class IRBlock {
    private IRFunction parentFunc;

    private String name;

    private LinkedList<IRInst> insts;
    private TerminalInst tailInst;

    private Set<IRBlock> precursors;
    private Set<IRBlock> successors;

    public IRBlock(IRFunction parentFunc, String name) {
        insts = new LinkedList<>();
        tailInst = null;
        this.parentFunc = parentFunc;
        this.name = name;

        precursors = new LinkedHashSet<>();
        successors = new LinkedHashSet<>();
    }

    public IRFunction getParentFunc() {
        return parentFunc;
    }

    public String getName() {
        return name;
    }

    public Set<IRBlock> getPrecursors() {
        return precursors;
    }

    public Set<IRBlock> getSuccessors() {
        return successors;
    }

    public LinkedList<IRInst> getInsts() {
        return insts;
    }

    public void setTailInst(TerminalInst tailInst) {
        assert this.tailInst == null;
        this.tailInst = tailInst;
        successors.clear();
        if(tailInst instanceof BrInst) {
            var trueBlock = ((BrInst) tailInst).getTrueBlock();
            var falseBlock = ((BrInst) tailInst).getFalseBlock();
            successors.add(trueBlock);
            successors.add(falseBlock);
            trueBlock.precursors.add(this);
            falseBlock.precursors.add(this);
        }
        // else RetInst: do nothing.
    }

    public TerminalInst getTailInst() {
        return tailInst;
    }

    public void appendInst(IRInst newInst) {
        insts.add(newInst);
        newInst.setParentBlock(this);
    }

    public void insertInstBefore(IRInst inst0, IRInst newInst) {
        assert insts.contains(inst0);
        insts.add(insts.indexOf(inst0), newInst);
    }

    public void insertInstAfter(IRInst inst0, IRInst newInst) {
        assert insts.contains(inst0);
        insts.add(insts.indexOf(inst0) + 1, newInst);
    }
}
