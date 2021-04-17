package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

public class Unary extends ASMInst {
    public enum Operator {
        seqz, snez, sltz, sgtz
    }

    private Operator operator;
    private VirtualRegister rd;
    private VirtualRegister rs;

    public Unary(ASMBlock parentBlock, Operator operator, VirtualRegister rd, VirtualRegister rs) {
        super(parentBlock);
        this.operator = operator;
        this.rd = rd;
        this.rs = rs;

        this.rd.addDef(this);
        this.rs.addUse(this);
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public VirtualRegister getRs() {
        return rs;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(rs);
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
        if (rs == oldVR) rs = newVR;
        else throw new RuntimeException();
    }

    @Override
    public String emit() {
        return "\t" + operator + "\t" + rd.emit() + ", " + rs.emit();
    }

    @Override
    public String toString() {
        return operator + "\t" + rd.toString() + ", " + rs.toString();
    }
}
