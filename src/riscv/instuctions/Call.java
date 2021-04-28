package riscv.instuctions;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

public class Call extends ASMInst {
    private ASMFunction function;

    public Call(ASMBlock parentblock, ASMFunction function) {
        super(parentblock);
        this.function = function;

        // FIXME
//        for (int i = 0; i < Integer.min(8, function.getParams().size()); ++i) {
//            PhysicalRegister.argVRs.get(i).addUse(this);
//        }
        for (String name : PhysicalRegister.callerSavePRNames) {
            PhysicalRegister.vrs.get(name).addDef(this);
        }
    }

    public ASMFunction getFunction() {
        return function;
    }

//    @Override
//    public HashSet<VirtualRegister> getUses() {
//        HashSet<VirtualRegister> ret = new HashSet<>();
//        for (int i = 0; i < Integer.min(function.getParams().size(), 8); ++i)
//            ret.add(PhysicalRegister.argVRs.get(i));
//        return ret;
//    }

    @Override
    public HashSet<VirtualRegister> getDefs() {
        HashSet<VirtualRegister> ret = new HashSet<>();
        for (String name : PhysicalRegister.callerSavePRNames) {
            ret.add(PhysicalRegister.vrs.get(name));
        }
        return ret;
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
