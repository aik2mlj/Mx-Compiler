package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Immediate;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
import java.util.Set;

public class Li extends ASMInst {
    // Load Immediate
    private VirtualRegister rd;
    private Immediate imm;

    public Li(ASMBlock parentBlock, VirtualRegister rd, Immediate imm) {
        super(parentBlock);
        this.rd = rd;
        this.imm = imm;

        this.rd.addDef(this);
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public Immediate getImm() {
        return imm;
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
        return "\tli\t" + rd.emit() + ", " + imm.emit();
    }

    @Override
    public String toString() {
        return "li\t" + rd.toString() + ", " + imm.toString();
    }
}
