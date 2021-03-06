package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
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

    public void removeFromBlock() {
        dst.removeDef(this);
        src.removeUse(this);
        dst = src = null;
        if (next != null)
            next.prev = prev;
        else parentBlock.setTailInst(prev);
        if (prev != null)
            prev.next = next;
        else parentBlock.setHeadInst(next);
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
        ret.add(src);
        return ret;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
        ret.add(dst);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceDef(oldVR, newVR);
        if (dst == oldVR) dst = newVR;
        else throw new RuntimeException();
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceUse(oldVR, newVR);
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
