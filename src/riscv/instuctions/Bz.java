package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

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
    public String emit() {
        return "\t" + operator + "\t" + rs1.emit() + ", " + branchBlock.getName();
    }
}
