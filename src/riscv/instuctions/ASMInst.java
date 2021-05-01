package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
import java.util.Set;

abstract public class ASMInst {
    protected ASMBlock parentBlock;

    public ASMInst prev, next;

    private boolean fake = false;

    public ASMInst(ASMBlock parentBlock) {
        this.parentBlock = parentBlock;
        prev = next = null;
    }

    public void addPrev(ASMInst newInst) {
        newInst.prev = prev;
        newInst.next = this;
        if (prev != null)
            prev.next = newInst;
        else parentBlock.setHeadInst(newInst);
        prev = newInst;
    }

    public void addNext(ASMInst newInst) {
        newInst.next = next;
        newInst.prev = this;
        if (next != null)
            next.prev = newInst;
        else parentBlock.setTailInst(newInst);
        next = newInst;
    }

    public ASMBlock getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(ASMBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    public Set<VirtualRegister> getUses() {
        return new LinkedHashSet<>();
    }

    public Set<VirtualRegister> getDefs() {
        return new LinkedHashSet<>();
    }

    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
        oldVR.removeDef(this);
        newVR.addDef(this);
    }

    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        oldVR.removeUse(this);
        newVR.addUse(this);
    }

    abstract public String emit();

    public void setFake() {
        this.fake = true;
    }

    public boolean isFake() {
        return fake;
    }

    @Override
    public abstract String toString();
}
