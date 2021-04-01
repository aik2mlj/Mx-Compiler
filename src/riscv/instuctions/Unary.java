package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

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
    public String emit() {
        return "\t" + operator + "\t" + rd.emit() + ", " + rs.emit();
    }
}
