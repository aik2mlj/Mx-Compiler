package riscv.operands.register;

import riscv.instuctions.ASMInst;
import riscv.instuctions.Move;
import riscv.operands.StackAddr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class VirtualRegister extends Register {
    private String name;
    private PhysicalRegister trueReg;

    private boolean isAollocated = false;

    private Set<ASMInst> uses;
    private Set<ASMInst> defs;

    private HashSet<VirtualRegister> adjList = new HashSet<>();
    private HashSet<Move> moveList = new HashSet<>();
    public int degree = 0;
    private double spillCost = 0;
    private VirtualRegister alias = null;

    public VirtualRegister(String name) {
        this.name = name;
        trueReg = null;
        this.uses = new HashSet<>();
        this.defs = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(PhysicalRegister trueReg) {
        this.trueReg = trueReg;
    }

    public PhysicalRegister getColor() {
        return trueReg;
    }

    public void addUse(ASMInst inst) {
        uses.add(inst);
    }

    public void addDef(ASMInst inst) {
        defs.add(inst);
    }

    public Set<ASMInst> getUses() {
        return uses;
    }

    public Set<ASMInst> getDefs() {
        return defs;
    }

    public void removeDef(ASMInst inst) {
        defs.remove(inst);
    }

    public void removeUse(ASMInst inst) {
        uses.remove(inst);
    }

    public void setAollocated() {
        isAollocated = true;
    }

    public boolean isAollocated() {
        return isAollocated;
    }

    public double getSpillCost() {
        return spillCost;
    }

    public HashSet<Move> getMoveList() {
        return moveList;
    }

    public HashSet<VirtualRegister> getAdjList() {
        return adjList;
    }

    public VirtualRegister getAlias() {
        return alias;
    }

    public void setAlias(VirtualRegister alias) {
        this.alias = alias;
    }

    public void clearColorData() {
        adjList.clear();
        moveList.clear();
        degree = 0;
        spillCost = 0;
        alias = null;
        trueReg = null;
    }

    @Override
    public String emit() {
//        return name;
        return trueReg.emit();
    }

    @Override
    public String toString() {
//        return name;
        return name.replaceAll("_spill([0-9]+)", "");
    }
}
