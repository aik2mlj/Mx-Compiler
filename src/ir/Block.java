package ir;

import ir.instruction.BrInst;
import ir.instruction.Inst;
import ir.instruction.TerminalInst;
import riscv.ASMBlock;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class Block {
    private Function parentFunc;

    private String name;

    private LinkedList<Inst> insts;
    private TerminalInst tailInst;

    private Set<Block> predecessors;
    private Set<Block> successors;

    private ASMBlock asmBlock;

    public Block(Function parentFunc, String name) {
        insts = new LinkedList<>();
        tailInst = null;
        this.parentFunc = parentFunc;
        this.name = name;

        predecessors = new LinkedHashSet<>();
        successors = new LinkedHashSet<>();
    }

    public Function getParentFunc() {
        return parentFunc;
    }

    public String getName() {
        return name;
    }

    public Set<Block> getPredecessors() {
        return predecessors;
    }

    public Set<Block> getSuccessors() {
        return successors;
    }

    public LinkedList<Inst> getInsts() {
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
            trueBlock.predecessors.add(this);
            if (falseBlock != null) {
                successors.add(falseBlock);
                falseBlock.predecessors.add(this);
            }
        }
        // else RetInst: do nothing.
    }

    public TerminalInst getTailInst() {
        return tailInst;
    }

    public void appendInst(Inst newInst) {
        if (tailInst == null) {
            insts.add(newInst);
            newInst.setParentBlock(this);
            if (newInst instanceof TerminalInst)
                setTailInst((TerminalInst) newInst);
            newInst.addUseAndDef();
        }
    }

    public void pushFrontInst(Inst newInst) {
        insts.addFirst(newInst);
        newInst.setParentBlock(this);
        newInst.addUseAndDef();
    }

    public void insertInstBefore(Inst inst0, Inst newInst) {
        assert insts.contains(inst0);
        insts.add(insts.indexOf(inst0), newInst);
        newInst.setParentBlock(this);
        newInst.addUseAndDef();
    }

    public void insertInstAfter(Inst inst0, Inst newInst) {
        assert insts.contains(inst0);
        insts.add(insts.indexOf(inst0) + 1, newInst);
        newInst.setParentBlock(this);
        newInst.addUseAndDef();
    }

    public void appendBrInstTo_U(Block toBlock) {
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

    public void rename(String name) {
        this.name = name;
    }

    public void setAsmBlock(ASMBlock asmBlock) {
        this.asmBlock = asmBlock;
    }

    public ASMBlock getAsmBlock() {
        return asmBlock;
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
