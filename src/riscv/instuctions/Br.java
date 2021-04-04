package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
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
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(rs1);
        ret.add(rs2);
        return ret;
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
        if (rs1 == oldVR) rs1 = newVR;
        else if (rs2 == oldVR) rs2 = newVR;
        else throw new RuntimeException();
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
