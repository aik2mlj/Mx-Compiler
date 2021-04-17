package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.Set;

public class Ret extends ASMInst {
    public Ret(ASMBlock parentBlock) {
        super(parentBlock);
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {

    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {

    }

    @Override
    public String emit() {
        return "\tret";
    }

    @Override
    public String toString() {
        return "ret";
    }
}
