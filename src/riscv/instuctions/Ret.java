package riscv.instuctions;

import riscv.ASMBlock;

public class Ret extends ASMInst {
    public Ret(ASMBlock parentBlock) {
        super(parentBlock);
    }

    @Override
    public String emit() {
        return "\tret";
    }
}
