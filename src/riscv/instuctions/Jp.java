package riscv.instuctions;

import riscv.ASMBlock;

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
    public String emit() {
        return "\tj\t" + jumpBlock.getName();
    }
}
