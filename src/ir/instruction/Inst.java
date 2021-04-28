package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.Constant;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.HashSet;

abstract public class Inst {
    private Block parentBlock;

    public Inst next, prev;

    // ADCE
    private boolean isLive = true;

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
        parentBlock.refreshInstAddPhi(newInst);
        newInst.prev = prev;
        newInst.next = this;
        if (prev != null)
            prev.next = newInst;
        else parentBlock.setHeadInst(newInst);
        prev = newInst;
    }

    public void addNext(Inst newInst) {
        parentBlock.refreshInstAddPhi(newInst);
        newInst.next = next;
        newInst.prev = this;
        if (next != null)
            next.prev = newInst;
        else parentBlock.setTailInst(newInst);
        next = newInst;
    }

    public void removeFromBlock() {
        removeUse();
        if (this instanceof PhiInst)
            parentBlock.getPhiInsts().remove(this);
        if (next != null)
            next.prev = prev;
        else parentBlock.setTailInst(prev);
        if (prev != null)
            prev.next = next;
        else parentBlock.setHeadInst(next);
        next = prev = null;
        parentBlock = null;
    }

    public abstract void removeUse();

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

    public HashSet<Operand> getUses() {
        return new HashSet<>();
    }

    abstract public void accept(IRVisitor visitor);

    public abstract void replaceUse(Register original, Operand replaced);

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    @Override
    public abstract String toString();

    public boolean allUsesAreConst() {
        for (Operand use : getUses()) {
            if (!(use instanceof Constant))
                return false;
        }
        return true;
    }
}
