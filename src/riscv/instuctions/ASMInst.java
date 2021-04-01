package riscv.instuctions;

import riscv.ASMBlock;

abstract public class ASMInst {
    private ASMBlock parentBlock;

    public ASMInst(ASMBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    public ASMBlock getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(ASMBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    abstract public String emit();
}
