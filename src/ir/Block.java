package ir;

import ir.instruction.BrInst;
import ir.instruction.Inst;
import ir.instruction.PhiInst;
import ir.instruction.TerminalInst;
import riscv.ASMBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Block {
    private Function parentFunc;

    private String name;

    private Inst headInst;
    private Inst tailInst;

    private Set<Block> predecessors;
    private Set<Block> successors;

    private ASMBlock asmBlock;

    // dominance things
    private Block iDom = null;
    private HashSet<Block> domFrontier = new HashSet<>();

    // phi
    private ArrayList<PhiInst> phiInsts = new ArrayList<>();
    private ResolvePhi.ParallelCopy parallelCopy;

    public Block(Function parentFunc, String name) {
        headInst = tailInst = null;
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

    public Inst getHeadInst() {
        return headInst;
    }

    public Inst getTailInst() {
        return tailInst;
    }

    public void setHeadInst(Inst headInst) {
        this.headInst = headInst;
    }

    public void setTailInst(Inst tailInst) {
        this.tailInst = tailInst;
    }

    public void setTerminal() {
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

    public void appendInst(Inst newInst) {
        if (tailInst instanceof TerminalInst)
            throw new RuntimeException();
        if (headInst == null)
            headInst = newInst;
        else {
            tailInst.next = newInst;
            newInst.prev = tailInst;
        }
        tailInst = newInst;
        if (newInst instanceof TerminalInst)
            setTerminal();
        newInst.setParentBlock(this);
        newInst.addUseAndDef();
    }

    public void pushFrontInst(Inst newInst) {
        if (headInst == null)
            tailInst = newInst;
        else {
            newInst.next = headInst;
            headInst.prev = newInst;
        }
        headInst = newInst;
        newInst.setParentBlock(this);
        newInst.addUseAndDef();
    }

    public void appendBrInstTo_U(Block toBlock) {
        // unconditional BrInst
        if (!(tailInst instanceof TerminalInst)) {
            var brInst = new BrInst(this, null, toBlock, null);
            appendInst(brInst);
            setTerminal();
        }
    }

    // TODO: set tail polishing


    public Block getiDom() {
        return iDom;
    }

    public void setiDom(Block iDom) {
        this.iDom = iDom;
    }

    public HashSet<Block> getDomFrontier() {
        return domFrontier;
    }

    public ArrayList<PhiInst> getPhiInsts() {
        return phiInsts;
    }

    public void setParallelCopy(ResolvePhi.ParallelCopy parallelCopy) {
        this.parallelCopy = parallelCopy;
    }

    public ResolvePhi.ParallelCopy getParallelCopy() {
        return parallelCopy;
    }

    public void replaceSuc(Block origin, Block replaced) {
        origin.predecessors.remove(this);
        assert tailInst instanceof BrInst;
        if (((BrInst) tailInst).getTrueBlock() == origin) {
            ((BrInst) tailInst).setTrueBlock(replaced);
        } else if (((BrInst) tailInst).getFalseBlock() == origin)
            ((BrInst) tailInst).setFalseBlock(replaced);
        setTerminal();
    }

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

    public void mergeInto(Block pred) {
        pred.getTailInst().removeFromBlock();
        successors.forEach(suc -> suc.predecessors.remove(this));
        for (var inst = headInst; inst != null; inst = inst.next) {
            pred.appendInst(inst);
        }
        parentFunc.removeBlock(this); // delete this
    }
}
