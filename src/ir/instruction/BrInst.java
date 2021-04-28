package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.ConstBool;
import ir.operand.Constant;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.HashSet;

public class BrInst extends TerminalInst {
    private Operand condition;
    private Block trueBlock, falseBlock;

    public BrInst(Block parentBlock, Operand condition, Block trueBlock, Block falseBlock) {
        super(parentBlock);
        if (condition instanceof Constant) {
            assert condition instanceof ConstBool;
            this.condition = null;
            if (((ConstBool) condition).getValue()) {
                this.trueBlock = trueBlock;
                this.falseBlock = null;
            } else {
                this.trueBlock = falseBlock;
                this.falseBlock = null;
            }
        } else {
            this.condition = condition;
            this.trueBlock = trueBlock;
            this.falseBlock = falseBlock;
        }

    }

    public void setCondition(Operand condition) {
        this.condition = condition;
    }

    public Operand getCondition() {
        return condition;
    }

    @Override
    public void removeUse() {
        if (condition != null)
            condition.removeUse(this);
    }

    @Override
    public void addUseAndDef() {
        // TODO
        if (condition != null)
            condition.addUse(this);
    }

    @Override
    public HashSet<Operand> getUses() {
        HashSet<Operand> ret = new HashSet<>();
        if (condition != null)
            ret.add(condition);
        return ret;
    }

    public void setTrueBlock(Block trueBlock) {
        this.trueBlock = trueBlock;
    }

    public void setFalseBlock(Block falseBlock) {
        this.falseBlock = falseBlock;
    }

    public void replaceBlock(Block original, Block replaced) {
        if (trueBlock == original) {
            original.getPredecessors().remove(this.getParentBlock());
            trueBlock = replaced;
        }
        else if (falseBlock == original) {
            original.getPredecessors().remove(this.getParentBlock());
            falseBlock = replaced;
        }
        else throw new RuntimeException();
    }

    public Block getTrueBlock() {
        return trueBlock;
    }

    public Block getFalseBlock() {
        return falseBlock;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (condition == original) {
            condition.removeUse(this);
            condition = replaced;
            replaced.addUse(this);
        }
    }

    @Override
    public String toString() {
        return "br " + (getCondition() != null ? getCondition().toString() + ", " : "") +
                "label " + getTrueBlock().toString() + (getFalseBlock() != null ? ", label " + getFalseBlock().toString() : "");
    }
}
