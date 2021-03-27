package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.ConstNull;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.IntType;
import ir.type.PointerType;

public class IcmpInst extends IRInst {
    public enum Operator {
        eq, ne, sgt, sge, slt, sle
    }

    private Operator operator;
    private IROperand lhs, rhs;
    private IRType type;
    private Register dstReg;

    public IcmpInst(IRBlock parentBlock, Operator operator, IROperand lhs, IROperand rhs, Register dstReg) {
        super(parentBlock);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
        this.type = lhs.getType();
        this.dstReg = dstReg;
        assert lhs.getType().equals(rhs.getType()) || lhs.getType() instanceof PointerType && rhs instanceof ConstNull;
        assert dstReg.getType().equals(new IntType(IntType.BitWidth.i1));
    }

    public Operator getOperator() {
        return operator;
    }

    public Register getDstReg() {
        return dstReg;
    }

    public IROperand getLhs() {
        return lhs;
    }

    public IROperand getRhs() {
        return rhs;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
