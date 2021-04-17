package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Operand;
import ir.operand.Register;

abstract public class Inst {
    private Block parentBlock;

    public Inst next, prev;

    public Inst(Block parentBlock) {
        this.parentBlock = parentBlock;
        next = prev = null;
    }

    public Block getParentBlock() {
        return parentBlock;
    }

    public void setParentBlock(Block parentBlock) {
        this.parentBlock = parentBlock;
    }

    public void addPrev(Inst newInst) {
        newInst.prev = prev;
        newInst.next = this;
        if (prev != null)
            prev.next = newInst;
        else parentBlock.setHeadInst(newInst);
        prev = newInst;
        newInst.addUseAndDef();
    }

    public void addNext(Inst newInst) {
        newInst.next = next;
        newInst.prev = this;
        if (next != null)
            next.prev = newInst;
        else parentBlock.setTailInst(newInst);
        next = newInst;
        newInst.addUseAndDef();
    }

    public void removeFromBlock() {
        removeUse();
        if (next != null)
            next.prev = prev;
        else parentBlock.setTailInst(prev);
        if (prev != null)
            prev.next = next;
        else parentBlock.setHeadInst(next);
        next = prev = null;
    }

    protected abstract void removeUse();

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

    public abstract void replaceUse(Register original, Operand replaced);
}
