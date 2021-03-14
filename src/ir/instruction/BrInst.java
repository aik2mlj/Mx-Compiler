package ir.instruction;

import ir.IRBlock;
import ir.operand.IROperand;

public class BrInst extends TerminalInst {
    private IROperand condition;
    private IRBlock trueBlock, falseBlock;

    public BrInst(IRBlock parentBlock, IROperand condition, IRBlock trueBlock, IRBlock falseBlock) {
        super(parentBlock);
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    public IROperand getCondition() {
        return condition;
    }

    public IRBlock getTrueBlock() {
        return trueBlock;
    }

    public IRBlock getFalseBlock() {
        return falseBlock;
    }
}
