package riscv;

import riscv.instuctions.ASMInst;
import riscv.operands.register.VirtualRegister;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ASMBlock {
    private ASMFunction function;
    private String irName;
    private String name;

    private ASMInst headInst, tailInst;

    private Set<ASMBlock> predecessors;
    private Set<ASMBlock> successors;

    // Liveness Analysis
    private HashSet<VirtualRegister> uses;
    private HashSet<VirtualRegister> defs;
    private HashSet<VirtualRegister> liveIn;
    private HashSet<VirtualRegister> liveOut;

    public ASMBlock(ASMFunction function, String irName, String name) {
        this.function = function;
        this.irName = irName;
        this.name = name;
        headInst = tailInst = null;

        predecessors = new HashSet<>();
        successors = new HashSet<>();

        liveIn = new HashSet<>();
        liveOut = new HashSet<>();
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

    public void setDefs(HashSet<VirtualRegister> defs) {
        this.defs = defs;
    }

    public void setUses(HashSet<VirtualRegister> uses) {
        this.uses = uses;
    }

    public HashSet<VirtualRegister> getDefs() {
        return defs;
    }

    public HashSet<VirtualRegister> getUses() {
        return uses;
    }

    public HashSet<VirtualRegister> getLiveIn() {
        return liveIn;
    }

    public HashSet<VirtualRegister> getLiveOut() {
        return liveOut;
    }

    public void setLiveOut(HashSet<VirtualRegister> liveOut) {
        this.liveOut = liveOut;
    }

    public void accept(ASMVisitor visitor) {
        visitor.visit(this);
    }
}
