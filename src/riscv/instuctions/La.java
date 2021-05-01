package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.GlobalVar;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
import java.util.Set;

public class La extends ASMInst {
    // LoadAddress
    private VirtualRegister rd;
    private GlobalVar globalVar;

    public La(ASMBlock parentBlock, VirtualRegister rd, GlobalVar globalVar) {
        super(parentBlock);
        this.rd = rd;
        this.globalVar = globalVar;

        this.rd.addDef(this);
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public GlobalVar getGlobalVar() {
        return globalVar;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
        ret.add(rd);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceDef(oldVR, newVR);
        if (rd == oldVR) rd = newVR;
        else throw new RuntimeException();
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {

    }

    @Override
    public String emit() {
        return "\tla\t" + rd.emit() + ", " + globalVar.getName();
    }

    @Override
    public String toString() {
        return "la\t" + rd.toString() + ", " + globalVar.getName();
    }
}
