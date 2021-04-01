package riscv.instuctions;

import riscv.ASMBlock;
import riscv.ASMFunction;

public class Call extends ASMInst {
    private ASMFunction function;

    public Call(ASMBlock parentblock, ASMFunction function) {
        super(parentblock);
        this.function = function;
    }

    public ASMFunction getFunction() {
        return function;
    }

    @Override
    public String emit() {
        return "\tcall\t" + function.getName();
    }
}
