package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;

abstract public class IRInst {
    private IRBlock parentBlock;

    public IRInst(IRBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    public IRBlock getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(IRBlock parentBlock) {
        this.parentBlock = parentBlock;
    }

    abstract public void accept(IRVisitor visitor);
}
