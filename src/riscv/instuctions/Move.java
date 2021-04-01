package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

public class Move extends ASMInst {
    VirtualRegister dst;
    VirtualRegister src;

    public Move(ASMBlock parentBlock, VirtualRegister dst, VirtualRegister src) {
        super(parentBlock);
        this.dst = dst;
        this.src = src;
    }

    public VirtualRegister getDst() {
        return dst;
    }

    public VirtualRegister getSrc() {
        return src;
    }

    @Override
    public String emit() {
        return "\tmv\t" + dst.emit() + ", " + src.emit();
    }
}
