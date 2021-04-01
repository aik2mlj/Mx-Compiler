package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Immediate;
import riscv.operands.register.VirtualRegister;

public class Li extends ASMInst {
    // Load Immediate
    private VirtualRegister rd;
    private Immediate imm;

    public Li(ASMBlock parentBlock, VirtualRegister rd, Immediate imm) {
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
        return "\tli\t" + rd.emit() + ", " + imm.emit();
    }
}
