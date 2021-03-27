package ir.instruction;

import ir.IRBlock;
import ir.IRVisitor;
import ir.operand.GlobalVar;
import ir.operand.IROperand;
import ir.operand.Register;
import ir.type.ArrayType;
import ir.type.IntType;
import ir.type.PointerType;

import java.util.ArrayList;

public class GetElementPtrInst extends IRInst {
    private Register dstReg; // returns a pointer
    private IROperand pointer;
    private ArrayList<IROperand> indices;

    public GetElementPtrInst(IRBlock parentBlock, IROperand pointer, ArrayList<IROperand> indices, Register dstReg) {
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

    public IROperand getPointer() {
        return pointer;
    }

    public Register getDstReg() {
        return dstReg;
    }

    public ArrayList<IROperand> getIndices() {
        return indices;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
