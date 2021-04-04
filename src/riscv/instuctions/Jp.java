package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.Set;

public class Jp extends ASMInst {
    private ASMBlock jumpBlock;

    public Jp(ASMBlock parentBlock, ASMBlock jumpBlock) {
        super(parentBlock);
        this.jumpBlock = jumpBlock;
    }

    public ASMBlock getJumpBlock() {
        return jumpBlock;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        return null;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        return null;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {

    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {

    }

    @Override
    public String emit() {
        return "\tj\t" + jumpBlock.getName();
    }

    @Override
    public String toString() {
        return "j\t" + jumpBlock.getName();
    }
}
