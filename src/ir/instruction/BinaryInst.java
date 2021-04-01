package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

public class BinaryInst extends Inst {
    public enum Operator {
        add, sub, mul, sdiv, srem, // sdiv: Div; srem: mod
        shl, ashr, and, or, xor
    }

    private Operator operator;
    private Operand lhs, rhs;
    private Register dstReg;

    public BinaryInst(Block parentBlock, Operator operator, Operand lhs, Operand rhs, Register dstReg) {
        super(parentBlock);
        this.operator = operator;
        this.lhs = lhs; this.rhs = rhs;
        this.dstReg = dstReg;
    }

    public Operator getOperator() {
        return operator;
    }

    public Operand getLhs() {
        return lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        lhs.addUse(this);
        rhs.addUse(this);
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
