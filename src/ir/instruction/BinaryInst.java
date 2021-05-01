package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Constant;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.LinkedHashSet;

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
        switch (operator) {
            case add, mul, and, or, xor -> {
                if (lhs instanceof Constant && !(rhs instanceof Constant)) {
                    this.lhs = rhs;
                    this.rhs = lhs;
                } else {
                    this.lhs = lhs;
                    this.rhs = rhs;
                }
            }
            default -> {
                this.lhs = lhs;
                this.rhs = rhs;
            }
        }
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
    public LinkedHashSet<Operand> getUses() {
        LinkedHashSet<Operand> ret = new LinkedHashSet<>();
        ret.add(lhs); ret.add(rhs);
        return ret;
    }

    @Override
    public void removeUse() {
        lhs.removeUse(this);
        rhs.removeUse(this);
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (lhs == original) { lhs.removeUse(this); lhs = replaced; replaced.addUse(this); }
        if (rhs == original) { rhs.removeUse(this); rhs = replaced; replaced.addUse(this); }
    }

    @Override
    public String toString() {
        return getDstReg().toStringWithoutType() + " = " + getOperator().toString() + " " +
                getLhs().toString() + ", " + getRhs().toStringWithoutType();
    }

    @Override
    public Inst cloneInst(Block block) {
        var symbolTable = block.getParentFunc().getSymbolTable();
        Register dstReg = (Register) symbolTable.getClonedOperand(getDstReg());
        Operand lhs = symbolTable.getClonedOperand(this.lhs), rhs = symbolTable.getClonedOperand(this.rhs);
        return new BinaryInst(block, operator, lhs, rhs, dstReg);
    }

    @Override
    public boolean sameMeaning(Inst q) {
        if (q instanceof BinaryInst) {
            if (((BinaryInst) q).getOperator() == operator) {
                switch (operator) {
                    case add, mul, and, or, xor -> { // can commute
                        if (((BinaryInst) q).getLhs().equals(lhs) && ((BinaryInst) q).getRhs().equals(rhs) ||
                                ((BinaryInst) q).getLhs().equals(rhs) && ((BinaryInst) q).getRhs().equals(lhs))
                            return true;
                    }
                    default -> {
                        if (((BinaryInst) q).getLhs().equals(lhs) && ((BinaryInst) q).rhs.equals(rhs))
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
