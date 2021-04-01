package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Immediate;
import riscv.operands.RelocationImm;
import riscv.operands.register.VirtualRegister;

public class Lui extends ASMInst {
    private VirtualRegister rd;
    private Immediate imm;

    public Lui(ASMBlock parentBlock, VirtualRegister rd, Immediate imm) {
        super(parentBlock);
        this.rd = rd;
        this.imm = imm;
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public Immediate getImm() {
        return imm;
    }

    @Override
    public String emit() {
        return "\tlui\t" + rd.emit() + ", " + imm.emit();
    }
}
