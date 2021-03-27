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
        if (tailInst instanceof BrInst) {
            var trueBlock = ((BrInst) tailInst).getTrueBlock();
            var falseBlock = ((BrInst) tailInst).getFalseBlock();
            successors.add(trueBlock);
            trueBlock.precursors.add(this);
            if (falseBlock != null) {
                successors.add(falseBlock);
                falseBlock.precursors.add(this);
            }
        }
        // else RetInst: do nothing.
    }

    public TerminalInst getTailInst() {
        return tailInst;
    }

    public void appendInst(IRInst newInst) {
        assert tailInst == null;
        insts.add(newInst);
        newInst.setParentBlock(this);
        if (newInst instanceof TerminalInst)
            setTailInst((TerminalInst) newInst);
    }

    public void pushFrontInst(IRInst newInst) {
        insts.addFirst(newInst);
        newInst.setParentBlock(this);
    }

    public void insertInstBefore(IRInst inst0, IRInst newInst) {
        assert insts.contains(inst0);
        insts.add(insts.indexOf(inst0), newInst);
        newInst.setParentBlock(this);
    }

    public void insertInstAfter(IRInst inst0, IRInst newInst) {
        assert insts.contains(inst0);
        insts.add(insts.indexOf(inst0) + 1, newInst);
        newInst.setParentBlock(this);
    }

    public void appendBrInstTo_U(IRBlock toBlock) {
        // unconditional BrInst
        if (this.tailInst == null) {
            var tailInst = new BrInst(this, null, toBlock, null);
            appendInst(tailInst);
            setTailInst(tailInst);
        }
    }
    // TODO: set tail polishing

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
