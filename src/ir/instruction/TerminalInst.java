package ir.instruction;

import ir.IRBlock;

abstract public class TerminalInst extends IRInst {
    public TerminalInst(IRBlock parentBlock) {
        super(parentBlock);
    }
}
