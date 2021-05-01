package ir.instruction;

import ir.Block;
import ir.IRVisitor;
import ir.operand.GlobalVar;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.ArrayType;
import ir.type.IntType;
import ir.type.PointerType;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class GetElementPtrInst extends Inst {
    private Register dstReg; // returns a pointer
    private Operand pointer;
    private ArrayList<Operand> indices;

    public GetElementPtrInst(Block parentBlock, Operand pointer, ArrayList<Operand> indices, Register dstReg) {
        super(parentBlock);
        assert pointer.getType() instanceof PointerType
                || (pointer instanceof GlobalVar && pointer.getType() instanceof ArrayType);
        if (pointer.getType() instanceof PointerType)
            assert dstReg.getType() instanceof PointerType;
        else
            assert dstReg.getType().equals(new PointerType(new IntType(IntType.BitWidth.i8)));
        this.pointer = pointer;
        this.indices = indices;
        this.dstReg = dstReg;
    }

    public Operand getPointer() {
        return pointer;
    }

    @Override
    public void addUseAndDef() {
        dstReg.setDefInst(this);
        pointer.addUse(this);
        for (Operand index : indices) {
            index.addUse(this);
        }
    }

    @Override
    public LinkedHashSet<Operand> getUses() {
        LinkedHashSet<Operand> ret = new LinkedHashSet<>(indices);
        ret.add(pointer);
        return ret;
    }

    @Override
    public void removeUse() {
        pointer.removeUse(this);
        for (Operand index : indices) {
            index.removeUse(this);
        }
    }

    @Override
    public Register getDstReg() {
        return dstReg;
    }

    public ArrayList<Operand> getIndices() {
        return indices;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void replaceUse(Register original, Operand replaced) {
        if (pointer == original) {
            original.removeUse(this);
            pointer = replaced;
            replaced.addUse(this);
        }
        for (int i = 0; i < indices.size(); ++i)
            if (indices.get(i) == original) {
                indices.get(i).removeUse(this);
                indices.set(i, replaced);
                replaced.addUse(this);
            }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(getDstReg().toStringWithoutType() + " = getelementptr " +
                ((PointerType) getPointer().getType()).getBaseType().toString() + ", " +
                getPointer().toString() + ", ");
        for (int i = 0; i < getIndices().size(); ++i) {
            ret.append(getIndices().get(i).toString());
            if (i != getIndices().size() - 1)
                ret.append(", ");
        }
        return ret.toString();
    }

    @Override
    public Inst cloneInst(Block block) {
        var symbolTable = block.getParentFunc().getSymbolTable();
        Register dstReg = (Register) symbolTable.getClonedOperand(getDstReg());
        Operand pointer = symbolTable.getClonedOperand(this.pointer);
        var indices = new ArrayList<Operand>();
        this.indices.forEach(index -> indices.add(symbolTable.getClonedOperand(index)));
        return new GetElementPtrInst(block, pointer, indices, dstReg);
    }

    @Override
    public boolean sameMeaning(Inst q) {
        if (q instanceof GetElementPtrInst) {
            if (!((GetElementPtrInst) q).getPointer().equals(pointer))
                return false;
            if (((GetElementPtrInst) q).getIndices().size() != indices.size())
                return false;
            for (int i = 0; i < indices.size(); ++i) {
                if (!((GetElementPtrInst) q).getIndices().get(i).equals(indices.get(i)))
                    return false;
            }
            return true;
        }
        return false;
    }
}
