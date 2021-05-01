package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Immediate;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
import java.util.Set;

public class IBinary extends Binary {
    public enum Operator {
        addi, slli, srai, andi, ori, xori, slti
    }
    private VirtualRegister rd;
    private VirtualRegister rs1;
    private Immediate imm;
    private Operator operator;

    public IBinary(ASMBlock parentBlock, Operator operator, VirtualRegister rd, VirtualRegister rs1, Immediate imm) {
        super(parentBlock);
        this.operator = operator;
        this.rd = rd;
        this.rs1 = rs1;
        this.imm = imm;

        this.rs1.addUse(this);
        this.rd.addDef(this);
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public VirtualRegister getRs1() {
        return rs1;
    }

    public Immediate getImm() {
        return imm;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
        ret.add(rs1);
        return ret;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
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
        else throw new RuntimeException();
    }

    @Override
    public String emit() {
        return "\t" + operator + "\t" + rd.emit() + ", " + rs1.emit() + ", " + imm.emit();
    }

    @Override
    public String toString() {
        return operator + "\t" + rd.toString() + ", " + rs1.toString() + ", " + imm.toString();
    }
}
