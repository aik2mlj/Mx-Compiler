package riscv;

import riscv.operands.GlobalVar;

public interface ASMVisitor {
    void visit(ASMModule module);
    void visit(ASMFunction function);
    void visit(ASMBlock block);
    void visit(GlobalVar globalVar);
}
