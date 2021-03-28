package ir.instruction;

import ir.Block;
import ir.IRVisitor;

abstract public class Inst {
    private Block parentBlock;

    public Inst(Block parentBlock) {
        this.parentBlock = parentBlock;
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block parentBlock) {
        this.parentBlock = parentBlock;
    }

    abstract public void accept(IRVisitor visitor);
}
