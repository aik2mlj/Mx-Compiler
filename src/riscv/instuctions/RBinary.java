package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

public class RBinary extends Binary {
    public enum Operator {
        add, sub, mul, div, rem, sll, sra, and, or, xor, slt
    }
    private VirtualRegister rd;
    private VirtualRegister rs1;
    private VirtualRegister rs2;
    private Operator operator;

    public RBinary(ASMBlock parentBlock, Operator operator, VirtualRegister rd, VirtualRegister rs1, VirtualRegister rs2) {
        super(parentBlock);
        this.operator = operator;
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;

        this.rd.addDef(this);
        this.rs1.addUse(this);
        this.rs2.addUse(this);
    }

    public VirtualRegister getRs1() {
        return rs1;
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public VirtualRegister getRs2() {
        return rs2;
    }

    public Operator getOperator() {
        return operator;
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
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(rd);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceDef(oldVR, newVR);
        if (rd == oldVR) rd = newVR;
        else throw new RuntimeException();
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceUse(oldVR, newVR);
        if (rs1 == oldVR) rs1 = newVR;
        else if (rs2 == oldVR) rs2 = newVR;
        else throw new RuntimeException();
    }

    @Override
    public String emit() {
        return "\t" + operator + "\t" + rd.emit() + ", " + rs1.emit() + ", " + rs2.emit();
    }

    @Override
    public String toString() {
        return operator + "\t" + rd.toString() + ", " + rs1.toString() + ", " + rs2.toString();
    }
}
