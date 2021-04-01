package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

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

    public Register getDstReg() {
        return null;
    }

    public boolean hasDstReg() {
        if (this instanceof CallInst)
            return ((CallInst) this).getDstReg() != null;
        else return this instanceof AllocaInst ||
                this instanceof BinaryInst ||
                this instanceof BitcastToInst ||
                this instanceof GetElementPtrInst ||
                this instanceof IcmpInst ||
                this instanceof LoadInst ||
                this instanceof PhiInst ||
                this instanceof MoveInst;
    }

    abstract public void addUseAndDef();

    abstract public void accept(IRVisitor visitor);
}
