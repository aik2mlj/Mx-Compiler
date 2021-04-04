package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

abstract public class ASMInst {
    private ASMBlock parentBlock;

    private boolean fake = false;

    public ASMInst(ASMBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    public ASMBlock getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(ASMBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    abstract public Set<VirtualRegister> getUses();

    abstract public Set<VirtualRegister> getDefs();

    abstract public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR);

    abstract public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR);

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
