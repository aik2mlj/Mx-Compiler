package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.IROperand;

public class BrInst extends TerminalInst {
    private IROperand condition;
    private Block trueBlock, falseBlock;

    public BrInst(Block parentBlock, IROperand condition, Block trueBlock, Block falseBlock) {
        super(parentBlock);
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    public IROperand getCondition() {
        return condition;
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
