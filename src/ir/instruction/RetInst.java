package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.IROperand;
import ir.type.IRType;

public class RetInst extends TerminalInst {
    private IRType retType;
    private IROperand retValue;

    public RetInst(Block parentBlock, IRType retType, IROperand retValue) {
        super(parentBlock);
        this.retType = retType;
        this.retValue = retValue;
    }

    public IRType getRetType() {
        return retType;
    }

    public IROperand getRetValue() {
        return retValue;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
