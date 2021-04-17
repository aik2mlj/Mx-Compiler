package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

public class Bz extends ASMInst {
    public enum Operator {
        beqz, bnez, blez, bgez, bltz, bgtz
    }

    private Operator operator;
    private VirtualRegister rs1;
    private ASMBlock branchBlock;

    public Bz(ASMBlock parentBlock, Operator operator, VirtualRegister rs1, ASMBlock branchBlock) {
        super(parentBlock);
        this.operator = operator;
        this.rs1 = rs1;
        this.branchBlock = branchBlock;

        this.rs1.addUse(this);
    }

    public Operator getOperator() {
        return operator;
    }

    public VirtualRegister getRs1() {
        return rs1;
    }

    public ASMBlock getBranchBlock() {
        return branchBlock;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(rs1);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceUse(oldVR, newVR);
        if (rs1 == oldVR) rs1 = newVR;
        else throw new RuntimeException();
    }

    @Override
    public String emit() {
        return "\t" + operator + "\t" + rs1.emit() + ", " + branchBlock.getName();
    }

    @Override
    public String toString() {
        return operator + "\t" + rs1.toString() + ", " + branchBlock.getName();
    }
}
