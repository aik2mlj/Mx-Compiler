package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.operand.Register;

public class BinaryInst extends IRInst {
    public enum Operator {
        add, sub, mul, sdiv, srem, // sdiv: Div; srem: mod
        shl, ashr, and, or, xor
    }

    private Operator operator;
    private IROperand lhs, rhs;
    private Register dstReg;

    public BinaryInst(IRBlock parentBlock, Operator operator, IROperand lhs, IROperand rhs, Register dstReg) {
        super(parentBlock);
        this.operator = operator;
        this.lhs = lhs; this.rhs = rhs;
        this.dstReg = dstReg;
    }

    public Operator getOperator() {
        return operator;
    }

    public IROperand getLhs() {
        return lhs;
    }

    public IROperand getRhs() {
        return rhs;
    }

    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
