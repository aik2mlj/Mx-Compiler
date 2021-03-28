package ir.instruction;

import ir.Block;

abstract public class TerminalInst extends Inst {
    public TerminalInst(Block parentBlock) {
        super(parentBlock);
    }
}
