package backend;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.ASMModule;
import riscv.ASMVisitor;
import riscv.instuctions.Call;
import riscv.instuctions.IBinary;
import riscv.instuctions.Ret;
import riscv.operands.GlobalVar;
import riscv.operands.IntImm;
import riscv.operands.register.PhysicalRegister;

public class StackOverFlow implements ASMVisitor {
    private int offset = 0;

    public StackOverFlow(ASMModule module) {
        module.accept(this);
    }

    @Override
    public void visit(ASMModule module) {
        module.getFuncMap().values().forEach(this::visit);
    }

    @Override
    public void visit(ASMFunction function) {
        offset = function.getStackFrame().getSize() * 4 >= 2048 ? function.getStackFrame().getSize() * 4 - 2032 : 0;
        if (offset > 0) {
            assert function.getEntryBlock().getHeadInst() instanceof IBinary;
            ((IBinary) function.getEntryBlock().getHeadInst()).setImm(new IntImm(-2032));
            for (ASMBlock block : function.getBlocks()) {
                if (block.getTailInst() instanceof Ret) {
                    assert block.getTailInst().prev instanceof IBinary;
                    ((IBinary) block.getTailInst().prev).setImm(new IntImm(2032));
                    break;
                }
            }
            function.getStackFrame().getSpillAddrMap().forEach((virtualRegister, stackAddr) -> stackAddr.decreaseOffset(offset));
            function.getStackFrame().getSelfParamAddrList().forEach(stackAddr -> stackAddr.decreaseOffset(offset));
            function.getStackFrame().getParamAddrMap().values().forEach(params -> params.forEach(param -> param.decreaseOffset(offset)));
            function.getBlocks().forEach(this::visit);
        }
    }

    @Override
    public void visit(ASMBlock block) {
        var sp = PhysicalRegister.vrs.get("sp");
        for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
            if (inst instanceof Call) { // sub sp & add sp
                inst.addPrev(new IBinary(block, IBinary.Operator.addi, sp, sp, new IntImm(-offset)));
                inst.addNext(new IBinary(block, IBinary.Operator.addi, sp, sp, new IntImm(offset)));
            }
        }
    }

    @Override
    public void visit(GlobalVar globalVar) {

    }
}
