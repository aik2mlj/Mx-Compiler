package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.GlobalVar;
import riscv.operands.register.VirtualRegister;

public class La extends ASMInst {
    // LoadAddress
    private VirtualRegister rd;
    private GlobalVar globalVar;

    public La(ASMBlock parentBlock, VirtualRegister rd, GlobalVar globalVar) {
        super(parentBlock);
        this.rd = rd;
        this.globalVar = globalVar;
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public GlobalVar getGlobalVar() {
        return globalVar;
    }

    @Override
    public String emit() {
        return "\tla\t" + rd.emit() + ", " + globalVar.emit();
    }
}
