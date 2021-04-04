package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

public class Move extends ASMInst {
    VirtualRegister dst;
    VirtualRegister src;

    public Move(ASMBlock parentBlock, VirtualRegister dst, VirtualRegister src) {
        super(parentBlock);
        this.dst = dst;
        this.src = src;

        this.dst.addDef(this);
        this.src.addUse(this);
    }

    public VirtualRegister getDst() {
        return dst;
    }

    public VirtualRegister getSrc() {
        return src;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(src);
        return ret;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(dst);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
        if (dst == oldVR) dst = newVR;
        else throw new RuntimeException();
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        if (src == oldVR) src = newVR;
        else throw new RuntimeException();
    }

    @Override
    public String emit() {
        return "\tmv\t" + dst.emit() + ", " + src.emit();
    }

    @Override
    public String toString() {
        return "mv\t" + dst.toString() + ", " + src.toString();
    }
}
