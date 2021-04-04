package riscv.instuctions;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.Set;

public class Call extends ASMInst {
    private ASMFunction function;

    public Call(ASMBlock parentblock, ASMFunction function) {
        super(parentblock);
        this.function = function;

        // FIXME
//        for (int i = 0; i < Integer.min(8, function.getParams().size()); ++i) {
//            this.addUse(PhysicalRegister.argVRs.get(i));
//        }
        for (String name : PhysicalRegister.callerSavePRNames) {
            PhysicalRegister.vrs.get(name).addDef(this);
        }
    }

    public ASMFunction getFunction() {
        return function;
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
        return "\tcall\t" + function.getName();
    }

    @Override
    public String toString() {
        return "call\t" + function.getName();
    }
}
