package riscv;

import riscv.instuctions.ASMInst;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ASMBlock {
    private ASMFunction function;
    private String irName;
    private String name;

    private LinkedList<ASMInst> insts;

    private Set<ASMBlock> predecessors;
    private Set<ASMBlock> successors;

    public ASMBlock(ASMFunction function, String irName, String name) {
        this.function = function;
        this.irName = irName;
        this.name = name;
        this.insts = new LinkedList<>();

        predecessors = new HashSet<>();
        successors = new HashSet<>();
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

    public LinkedList<ASMInst> getInsts() {
        return insts;
    }

    public void appendInst(ASMInst newInst) {
        insts.add(newInst);
        newInst.setParentBlock(this);
    }

    public void pushFrontInst(ASMInst newInst) {
        insts.addFirst(newInst);
        newInst.setParentBlock(this);
    }

    public void addInstBefore(ASMInst inst0, ASMInst newInst) {
        insts.add(insts.indexOf(inst0), newInst);
        newInst.setParentBlock(this);
    }

    public void addInstAfter(ASMInst inst0, ASMInst newInst) {
        insts.add(insts.indexOf(inst0) + 1, newInst);
        newInst.setParentBlock(this);
    }

    public void accept(ASMVisitor visitor) { visitor.visit(this); }
}
