package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

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
    public String emit() {
        return "\t" + operator + "\t" + rd.emit() + ", " + rs1.emit() + ", " + rs2.emit();
    }
}
