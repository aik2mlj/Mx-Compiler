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
}
