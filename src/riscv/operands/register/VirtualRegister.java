package riscv.operands.register;

import riscv.instuctions.ASMInst;
import riscv.operands.StackAddr;

import java.util.HashSet;
import java.util.Set;

public class VirtualRegister extends Register {
    private String name;
    private PhysicalRegister trueReg;

    private Set<ASMInst> uses;
    private Set<ASMInst> defs;

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

    public void setTrueReg(PhysicalRegister trueReg) {
        if (this.trueReg == null)
            this.trueReg = trueReg;
    }

    public PhysicalRegister getTrueReg() {
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

    @Override
    public String emit() {
//        return name;
        return trueReg.emit();
    }

    @Override
    public String toString() {
        return name;
    }
}
