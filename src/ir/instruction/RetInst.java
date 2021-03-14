package ir.instruction;

import ir.IRBlock;
import ir.operand.IROperand;
import ir.type.IRType;

public class RetInst extends TerminalInst {
    private IRType retType;
    private IROperand retValue;

    public RetInst(IRBlock parentBlock, IRType retType, IROperand retValue) {
        super(parentBlock);
        this.retType = retType;
        this.retValue = retValue;
    }

    public IROperand getRetValue() {
        return retValue;
    }
}
