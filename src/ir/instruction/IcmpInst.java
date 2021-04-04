package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.*;
import ir.type.IRType;
import ir.type.IntType;
import ir.type.PointerType;

public class IcmpInst extends Inst {
    public enum Operator {
        eq, ne, sgt, sge, slt, sle
    }

    private Operator operator;
    private Operand lhs, rhs;
    private IRType type;
    private Register dstReg;

    public IcmpInst(Block parentBlock, Operator operator, Operand lhs, Operand rhs, Register dstReg) {
        super(parentBlock);
        this.operator = operator;
        // swap if lhs is Constant && rhs is not.
        if (lhs instanceof Constant && !(rhs instanceof Constant)) {
            this.operator = switch (operator) {
                case slt -> Operator.sgt;
                case sgt -> Operator.slt;
                case sge -> Operator.sle;
                case sle -> Operator.sge;
                default -> operator;
            };
            this.lhs = rhs;
            this.rhs = lhs;
        } else {
            this.lhs = lhs;
            this.rhs = rhs;
        }
        this.type = lhs.getType();
        this.dstReg = dstReg;
        assert lhs.getType().equals(rhs.getType()) || lhs.getType() instanceof PointerType && rhs instanceof ConstNull;
        assert dstReg.getType().equals(new IntType(IntType.BitWidth.i1));
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        lhs.addUse(this);
        rhs.addUse(this);
    }

    public boolean onlyHasOneBranch() {
        return dstReg.getUse().size() == 1;
    }

    public void removeE() {
        // sge -> sgt, sle -> slt
        if (rhs instanceof ConstBool)
            return;
        assert rhs instanceof ConstInt;
        if (operator == Operator.sge) {
            operator = Operator.sgt;
            rhs = new ConstInt(IntType.BitWidth.i32, ((ConstInt) rhs).getValue() - 1);
        } else if (operator == Operator.sle) {
            operator = Operator.slt;
            rhs = new ConstInt(IntType.BitWidth.i32, ((ConstInt) rhs).getValue() + 1);
        }
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    public Operand getLhs() {
        return lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
