package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.register.VirtualRegister;

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
    public String emit() {
        return "\t" + operator + "\t" + rs1.emit() + ", " + rs2.emit() + ", " + branchBlock.getName();
    }
}
