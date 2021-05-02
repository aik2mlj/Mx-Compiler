package riscv;

import ir.Block;
import riscv.instuctions.ASMInst;
import riscv.operands.register.VirtualRegister;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class ASMBlock {
    private ASMFunction function;
    private Block irBlock;
    private String irName;
    private String name;

    private ASMInst headInst, tailInst;

    private Set<ASMBlock> predecessors;
    private Set<ASMBlock> successors;

    // Liveness Analysis
    private LinkedHashSet<VirtualRegister> uses;
    private LinkedHashSet<VirtualRegister> defs;
    private LinkedHashSet<VirtualRegister> liveIn;
    private LinkedHashSet<VirtualRegister> liveOut;

    public ASMBlock(ASMFunction function, Block irBlock, String name) {
        this.function = function;
        this.irBlock = irBlock;
        this.irName = irBlock.getName();
        this.name = name;
        headInst = tailInst = null;

        predecessors = new LinkedHashSet<>();
        successors = new LinkedHashSet<>();

        liveIn = new LinkedHashSet<>();
        liveOut = new LinkedHashSet<>();
    }

    public ASMFunction getFunction() {
        return function;
    }

    public String getName() {
        return name;
    }

    public String getIrName() {
        return irName;
    }

    public Set<ASMBlock> getPredecessors() {
        return predecessors;
    }

    public Set<ASMBlock> getSuccessors() {
        return successors;
    }

    public ASMInst getHeadInst() {
        return headInst;
    }

    public void setHeadInst(ASMInst headInst) {
        this.headInst = headInst;
    }

    public ASMInst getTailInst() {
        return tailInst;
    }

    public void setTailInst(ASMInst tailInst) {
        this.tailInst = tailInst;
    }

    public void appendInst(ASMInst newInst) {
        if (headInst == null)
            headInst = newInst;
        else {
            tailInst.next = newInst;
            newInst.prev = tailInst;
        }
        tailInst = newInst;
        newInst.setParentBlock(this);
    }

    public void pushFrontInst(ASMInst newInst) {
        if (headInst == null)
            tailInst = newInst;
        else {
            newInst.next = headInst;
            headInst.prev = newInst;
        }
        headInst = newInst;
        newInst.setParentBlock(this);
    }

    public void setDefs(LinkedHashSet<VirtualRegister> defs) {
        this.defs = defs;
    }

    public void setUses(LinkedHashSet<VirtualRegister> uses) {
        this.uses = uses;
    }

    public LinkedHashSet<VirtualRegister> getDefs() {
        return defs;
    }

    public LinkedHashSet<VirtualRegister> getUses() {
        return uses;
    }

    public LinkedHashSet<VirtualRegister> getLiveIn() {
        return liveIn;
    }

    public LinkedHashSet<VirtualRegister> getLiveOut() {
        return liveOut;
    }

    public void setLiveOut(LinkedHashSet<VirtualRegister> liveOut) {
        this.liveOut = liveOut;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }

    public Block getIRBlock() {
        return irBlock;
    }
}
