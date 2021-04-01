package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import riscv.operands.register.Register;

public class BrInst extends TerminalInst {
    private Operand condition;
    private Block trueBlock, falseBlock;

    public BrInst(Block parentBlock, Operand condition, Block trueBlock, Block falseBlock) {
        super(parentBlock);
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    public Operand getCondition() {
        return condition;
    }

    @Override
    public void addUseAndDef() {
        // TODO
    }

    public Block getTrueBlock() {
        return trueBlock;
    }

    public Block getFalseBlock() {
        return falseBlock;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
