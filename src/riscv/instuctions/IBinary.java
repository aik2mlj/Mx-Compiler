package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Immediate;
import riscv.operands.register.VirtualRegister;

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
    public String emit() {
        return "\t" + operator + "\t" + rd.emit() + ", " + rs1.emit() + ", " + imm.emit();
    }
}
