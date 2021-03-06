package ir;

import ir.instruction.*;
import riscv.ASMBlock;
import util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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
    private LinkedHashSet<Block> domFrontier = new LinkedHashSet<>();

    private Block reverseIDom = null;
    private LinkedHashSet<Block> ReverseDomFrontier = new LinkedHashSet<>();

    // phi
    private ArrayList<PhiInst> phiInsts = new ArrayList<>();
    private ResolvePhi.ParallelCopy parallelCopy;

    // ADCE
    private boolean isLive = false;

    // loop things
    private int loopDepth = 0;

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

    private void setTerminal() {
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

    public void refreshInstAddPhi(Inst inst) {
        inst.removeUse();
        inst.setParentBlock(this);
        inst.addUseAndDef();
        if (inst instanceof PhiInst)
            phiInsts.add((PhiInst) inst);
    }

    public void appendInst(Inst newInst) {
        refreshInstAddPhi(newInst);
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
    }

    public void pushFrontInst(Inst newInst) {
        refreshInstAddPhi(newInst);
        if (headInst == null)
            tailInst = newInst;
        else {
            newInst.next = headInst;
            headInst.prev = newInst;
        }
        headInst = newInst;
    }

    public void appendBrInstTo_U(Block toBlock) {
        // unconditional BrInst
        if (!(tailInst instanceof TerminalInst)) {
            var brInst = new BrInst(this, null, toBlock, null);
            appendInst(brInst);
            setTerminal();
        }
    }

    public void replaceBrInst(BrInst brInst) {
        refreshInstAddPhi(brInst);
        successors.forEach(suc -> suc.predecessors.remove(this));
        tailInst.removeUse();
        brInst.prev = tailInst.prev;
        brInst.next = null;
        if (tailInst.prev != null)
            tailInst.prev.next = brInst;
        else headInst = brInst;
        tailInst.next = tailInst.prev = null;
        tailInst = brInst;
        setTerminal();
    }

    // TODO: set tail polishing


    public Block getiDom() {
        return iDom;
    }

    public void setiDom(Block iDom) {
        this.iDom = iDom;
    }

    public LinkedHashSet<Block> getDomFrontier() {
        return domFrontier;
    }

    public Block getReverseIDom() {
        return reverseIDom;
    }

    public LinkedHashSet<Block> getReverseDomFrontier() {
        return ReverseDomFrontier;
    }

    public void setReverseIDom(Block reverseIDom) {
        this.reverseIDom = reverseIDom;
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
        assert tailInst instanceof BrInst;
        ((BrInst) tailInst).replaceBlock(origin, replaced);
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
        // change successors' PhiInsts & remove preds
        successors.forEach(suc -> {
            suc.getPhiInsts().forEach(phiInst -> phiInst.replaceBlock(this, pred));
            suc.predecessors.remove(this);
        });
        for (var inst = headInst; inst != null; inst = inst.next) {
            pred.appendInst(inst);
            if (inst instanceof RetInst)
                parentFunc.setRetBlock(pred); // exitBlock: the return Block
        }
        parentFunc.removeBlock(this); // delete this
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean cleanPhis() {
        // after SCCP, some branches are replaced with jump, so some phis may have unreachable predecessors.
        var phis = new ArrayList<>(phiInsts);
        boolean ret = false;
        for (PhiInst phiInst : phis) {
            ret |= phiInst.clean();
        }
        return ret;
    }

    public boolean dominates(Block block) {
        Block dom = block;
        do {
            if (dom == this) return true;
            dom = dom.iDom;
        } while (dom != dom.iDom);
        return false;
    }

    public int getLoopDepth() {
        return loopDepth;
    }

    public void setLoopDepth(int loopDepth) {
        this.loopDepth = loopDepth;
    }

    public Block splitBlockBy(CallInst callInst) {
        // split block by the callInst: create new block containing insts after the callInst.
        Block splitBlock = new Block(parentFunc, "splitblock." + callInst.getFunction().getName());
        for (Inst inst = callInst.next; inst != null;) {
            var next = inst.next;
            inst.removeFromBlock();
            splitBlock.appendInst(inst);
            if (inst instanceof RetInst)
                parentFunc.setRetBlock(splitBlock);
            inst = next;
        }
        successors.forEach(suc -> {
            suc.getPhiInsts().forEach(phiInst -> phiInst.replaceBlock(this, splitBlock));
            suc.getPredecessors().remove(this);
        });
        // no terminal inst in "this" yet.
        // not put into parentFunc yet.
        return splitBlock;
    }

    public void cloneInstsFrom(Block block) {
        for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
            var newInst = inst.cloneInst(this);
            appendInst(newInst);
        }
    }
}
