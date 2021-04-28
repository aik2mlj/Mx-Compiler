package ir;

import ir.instruction.*;
import ir.operand.*;
import ir.type.*;

public interface IRVisitor {
    void visit(Module module);
    void visit(Function function);
    void visit(Block block);

    // ----------------- instructions ------------------
    void visit(AllocaInst inst);
    void visit(BinaryInst inst);
    void visit(BitcastToInst inst);
    void visit(BrInst inst);
    void visit(CallInst inst);
    void visit(GetElementPtrInst inst);
    void visit(IcmpInst inst);
    void visit(LoadInst inst);
    void visit(PhiInst inst);
    void visit(RetInst inst);
    void visit(StoreInst inst);

    void visit(MoveInst inst);

//    // ----------------- Operands -------------------
//    void visit(ConstInt operand);
//    void visit(ConstBool operand);
//    void visit(ConstNull operand);
//    void visit(ConstString operand);
//    void visit(GlobalVar operand);
//    void visit(Parameter operand);
//    void visit(Register operand);
//
//    // ----------------- Types ---------------------
//    void visit(ArrayType type);
//    void visit(IntType type);
//    void visit(PointerType type);
//    void visit(StructType type);
//    void visit(VoidType type);
}
