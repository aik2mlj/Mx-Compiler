package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
import java.util.Set;

public class Br extends ASMInst {
    public enum Operator {
        beq, bne, ble, bge, blt, bgt
    }
    private Operator operator;
    private VirtualRegister rs1;
    private VirtualRegister rs2;
    private ASMBlock branchBlock;

    public Br(ASMBlock parentBlock, Operator operator, VirtualRegister rs1, VirtualRegister rs2, ASMBlock branchBlock) {
        super(parentBlock);
        this.operator = operator;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.branchBlock = branchBlock;

        this.rs1.addUse(this);
        this.rs2.addUse(this);
    }

    public VirtualRegister getRs1() {
        return rs1;
    }

    public VirtualRegister getRs2() {
        return rs2;
    }

    public ASMBlock getBranchBlock() {
        return branchBlock;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
        ret.add(rs1);
        ret.add(rs2);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceUse(oldVR, newVR);
        boolean ok = false;
        if (rs1 == oldVR) { rs1 = newVR; ok = true; }
        if (rs2 == oldVR) { rs2 = newVR; ok = true; }
        if (!ok) throw new RuntimeException();
    }


    @Override
    public String emit() {
        return "\t" + operator + "\t" + rs1.emit() + ", " + rs2.emit() + ", " + branchBlock.getName();
    }

    @Override
    public String toString() {
        return operator + "\t" + rs1.toString() + ", " + rs2.toString() + ", " + branchBlock.getName();
    }
}
