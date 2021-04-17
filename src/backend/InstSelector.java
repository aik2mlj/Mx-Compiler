package backend;

import ir.Block;
import ir.Function;
import ir.IRVisitor;
import ir.Module;
import ir.instruction.*;
import ir.operand.*;
import ir.operand.GlobalVar;
import ir.type.*;
import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.ASMModule;
import riscv.StackFrame;
import riscv.instuctions.*;
import riscv.operands.*;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.ArrayList;

import static ir.instruction.IcmpInst.Operator.*;

public class InstSelector implements IRVisitor {
    private ASMModule asmModule;
    private ASMFunction currentFunc;
    private ASMBlock currentBlock;

    public InstSelector() {
        asmModule = new ASMModule();
        currentBlock = null;
        currentFunc = null;
    }

    public ASMModule getAsmModule() {
        return asmModule;
    }

    static private boolean needToLoadImmediate(int value) {
        return value >= (1 << 11) || value < -(1 << 11);
    }

    private VirtualRegister getVRFromOperand(Operand operand) {
        if (operand instanceof ConstInt) {
            int value = ((ConstInt) operand).getValue();
            if (value == 0)
                return PhysicalRegister.zeroVR;
            else {
                VirtualRegister newVR = new VirtualRegister("const_int");
                currentFunc.getSymbolTable().addVR(newVR);
                if (needToLoadImmediate(value)) {
                    currentBlock.appendInst(new Li(currentBlock, newVR, new IntImm(value)));
                } else {
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, newVR,
                            PhysicalRegister.zeroVR, new IntImm(value)));
                }
                return newVR;
            }
        } else if (operand instanceof ConstBool) {
            boolean value = ((ConstBool) operand).getValue();
            if (value) {
                VirtualRegister newVR = new VirtualRegister("const_bool");
                currentFunc.getSymbolTable().addVR(newVR);
                currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, newVR, PhysicalRegister.zeroVR,
                        new IntImm(1)));
                return newVR;
            } else
                return PhysicalRegister.zeroVR;
        } else if (operand instanceof ConstNull)
            return PhysicalRegister.zeroVR;
        else if (operand instanceof Parameter)
            return currentFunc.getSymbolTable().getVR(((Parameter) operand).getName());
        else if (operand instanceof Register)
            return currentFunc.getSymbolTable().getVR(((Register) operand).getName());
        else {
            throw new RuntimeException();
        }
    }

    private ASMOperand getASMOperand(Operand operand) {
        if (operand instanceof ConstInt) {
            int value = ((ConstInt) operand).getValue();
            if (needToLoadImmediate(value))
                return getVRFromOperand(operand);
            else
                return new IntImm(value);
        } else if (operand instanceof ConstBool) {
            boolean value = ((ConstBool) operand).getValue();
            return new IntImm(value ? 1 : 0);
        } else if (operand instanceof ConstNull)
            return PhysicalRegister.zeroVR;
        else if (operand instanceof Register || operand instanceof Parameter)
            return getVRFromOperand(operand);
        else throw new RuntimeException();
    }

    @Override
    public void visit(Module module) {
        module.getGlobalVarMap().values().forEach(globalVar -> {
            var newGlobalVar = new riscv.operands.GlobalVar(globalVar.getName());
            assert globalVar.getType() instanceof PointerType;
            IRType type = ((PointerType) globalVar.getType()).getBaseType();
            if (type instanceof IntType) {
                if (((IntType) type).getBitWidth() == IntType.BitWidth.i1) {
                    assert globalVar.getInitValue() instanceof ConstBool;
                    newGlobalVar.setBool(((ConstBool) globalVar.getInitValue()).getValue());
                } else {
                    assert globalVar.getInitValue() instanceof ConstInt;
                    newGlobalVar.setInt(((ConstInt) globalVar.getInitValue()).getValue());
                }
            } else if (type instanceof PointerType) {
                assert globalVar.getInitValue() instanceof ConstNull;
                newGlobalVar.setInt(0);
            } else {
                assert globalVar.getInitValue() instanceof ConstString;
                newGlobalVar.setString(((ConstString) globalVar.getInitValue()).getValue());
            }
            asmModule.addGlobalVar(newGlobalVar);
        });
        module.getBuiltInFuncMap().values().forEach(builtInFunc -> {
            asmModule.addBuiltInFunc(new ASMFunction(asmModule, builtInFunc.getName(), builtInFunc));
        });
        module.getFuncMap().values().forEach(function -> {
            asmModule.addFunction(new ASMFunction(asmModule, function.getName(), function));
        });
        // traverse the functions
        module.getFuncMap().values().forEach(function -> function.accept(this));
    }

    @Override
    public void visit(Function function) {
        currentFunc = asmModule.getFunction(function.getName());
        currentBlock = currentFunc.getEntryBlock();

        // stack frame
        StackFrame stackFrame = new StackFrame(currentFunc);
        currentFunc.setStackFrame(stackFrame);

        // save RA
        VirtualRegister savedRA = new VirtualRegister(PhysicalRegister.raVR.getName() + "_save");
        currentFunc.getSymbolTable().addVR(savedRA);
        currentBlock.appendInst(new Move(currentBlock, savedRA, PhysicalRegister.raVR));

        // save calleeSave registers
        for (VirtualRegister vr : PhysicalRegister.calleeSaveVRs) {
            VirtualRegister savedVR = new VirtualRegister(vr.getName() + "_save");
            currentFunc.getSymbolTable().addVR(savedVR);
            currentBlock.appendInst(new Move(currentBlock, savedVR, vr));
        }

        // first 8 params: mv from a0-a8
        for (int i = 0; i < Integer.min(function.getParameters().size(), 8); ++i) {
            var param = function.getParameters().get(i);
            VirtualRegister vr = currentFunc.getSymbolTable().getVR(param.getName());
            currentBlock.appendInst(new Move(currentBlock, vr, PhysicalRegister.argVRs.get(i)));
        }
        // load spilled params: ld
        for (int i = 8; i < function.getParameters().size(); ++i) {
            var param = function.getParameters().get(i);
            VirtualRegister vr = currentFunc.getSymbolTable().getVR(param.getName());
            StackAddr newStackAddr = new StackAddr(vr.getName());
            stackFrame.addSelfParamPos(newStackAddr);
            currentBlock.appendInst(new Ld(currentBlock, Ld.ByteSize.lw, vr, newStackAddr));
        }

        function.getBlocks().forEach(block -> block.accept(this));
    }

    @Override
    public void visit(Block block) {
        currentBlock = currentFunc.getSymbolTable().getASMBlock(block.getName());
        for (var inst = block.getHeadInst(); inst != null; inst = inst.next)
            inst.accept(this);
    }

    @Override
    public void visit(AllocaInst inst) {
        // do nothing.
        // set allocated: is treated as stackAddr in RegAllocator
//        var vr = getVRFromOperand(inst.getDstReg());
//        vr.setAollocated();
        // --------FIXME: here change to malloc, but shouldn't really
//        int bytes = inst.getType().getBytes();
//        VirtualRegister allocSize = new VirtualRegister("alloc_size");
//        currentFunc.getSymbolTable().addVR(allocSize);
//        currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, allocSize, PhysicalRegister.zeroVR, new IntImm(bytes)));
//        currentBlock.appendInst(new Move(currentBlock, PhysicalRegister.argVRs.get(0), allocSize));
//        var mallocFunc = asmModule.getBuiltInFunction("malloc");
//        currentBlock.appendInst(new Call(currentBlock, mallocFunc));
//        currentBlock.appendInst(new Move(currentBlock, vr, PhysicalRegister.argVRs.get(0)));
    }

    @Override
    public void visit(BinaryInst inst) {
        var lhs = inst.getLhs();
        var rhs = inst.getRhs();
        VirtualRegister rs1 = getVRFromOperand(lhs);
        ASMOperand rs2_imm = getASMOperand(rhs);
        var rd = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());
        switch (inst.getOperator()) {
            case add -> {
                if (rs2_imm instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, rd, rs1, (Immediate) rs2_imm));
                else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.add, rd, rs1, (VirtualRegister) rs2_imm));
            }
            case and -> {
                if (rs2_imm instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.andi, rd, rs1, (Immediate) rs2_imm));
                else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.and, rd, rs1, (VirtualRegister) rs2_imm));

            }
            case or -> {
                if (rs2_imm instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.ori, rd, rs1, (Immediate) rs2_imm));
                else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.or, rd, rs1, (VirtualRegister) rs2_imm));

            }
            case xor -> {
                if (rs2_imm instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.xori, rd, rs1, (Immediate) rs2_imm));
                else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.xor, rd, rs1, (VirtualRegister) rs2_imm));

            }
            case sub -> {
                if (rs2_imm instanceof Immediate) {
                    ((IntImm) rs2_imm).turnNegative();
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, rd, rs1, (Immediate) rs2_imm));
                } else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.sub, rd, rs1, (VirtualRegister) rs2_imm));
            }
            case mul -> {
                // both are VRs
                rs2_imm = getVRFromOperand(rhs);
                currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.mul, rd, rs1, (VirtualRegister) rs2_imm));
            }
            case sdiv -> {
                rs2_imm = getVRFromOperand(rhs);
                currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.div, rd, rs1, (VirtualRegister) rs2_imm));
            }
            case srem -> {
                rs2_imm = getVRFromOperand(rhs);
                currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.rem, rd, rs1, (VirtualRegister) rs2_imm));
            }
            case shl -> {
                if (rs2_imm instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.slli, rd, rs1, (Immediate) rs2_imm));
                else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.sll, rd, rs1, (VirtualRegister) rs2_imm));
            }
            case ashr -> {
                if (rs2_imm instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.srai, rd, rs1, (Immediate) rs2_imm));
                else
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.sra, rd, rs1, (VirtualRegister) rs2_imm));
            }
            default -> {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void visit(BitcastToInst inst) {
        var src = currentFunc.getSymbolTable().getVR(inst.getSrc().getName());
        var dst = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());
        currentBlock.appendInst(new Move(currentBlock, dst, src));
    }

    @Override
    public void visit(BrInst inst) {
        if (inst.getCondition() != null) {
            var cond = inst.getCondition();
            var thenBlock = currentFunc.getSymbolTable().getASMBlock(inst.getTrueBlock().getName());
            var elseBlock = currentFunc.getSymbolTable().getASMBlock(inst.getFalseBlock().getName());

            if (cond instanceof Register && ((Register) cond).getDefInst() instanceof IcmpInst &&
                    ((IcmpInst) ((Register) cond).getDefInst()).onlyHasOneBranch()) {
                var icmp = (IcmpInst) ((Register) cond).getDefInst();
                var type = icmp.getLhs().getType();
                var lhs = icmp.getLhs();
                var rhs = icmp.getRhs();
                VirtualRegister rs1 = getVRFromOperand(lhs);
                VirtualRegister rs2;
                if (type instanceof IntType) {
                    if (rhs instanceof Constant) {
                        rs2 = getVRFromOperand(rhs);
                    } else
                        rs2 = currentFunc.getSymbolTable().getVR(rhs.getName());
                } else if (type instanceof PointerType) {
                    if (rhs instanceof Constant) {
                        assert rhs instanceof ConstNull;
                        rs2 = getVRFromOperand(rhs);
                    } else
                        rs2 = currentFunc.getSymbolTable().getVR(rhs.getName());
                } else
                    throw new RuntimeException();
                Br.Operator operator = switch (icmp.getOperator()) {
                    case eq -> Br.Operator.bne;
                    case ne -> Br.Operator.beq;
                    case sgt -> Br.Operator.ble;
                    case sge -> Br.Operator.blt;
                    case slt -> Br.Operator.bge;
                    case sle -> Br.Operator.bgt;
                };
                currentBlock.appendInst(new Br(currentBlock, operator, rs1, rs2, elseBlock));
                currentBlock.appendInst(new Jp(currentBlock, thenBlock));
            } else {
                VirtualRegister newVR = currentFunc.getSymbolTable().getVR(cond.getName());
                currentBlock.appendInst(new Bz(currentBlock, Bz.Operator.beqz, newVR, elseBlock));
                currentBlock.appendInst(new Jp(currentBlock, thenBlock));
            }
        } else {
            var thenBlock = currentFunc.getSymbolTable().getASMBlock(inst.getTrueBlock().getName());
            currentBlock.appendInst(new Jp(currentBlock, thenBlock));
        }
    }

    @Override
    public void visit(CallInst inst) {
        ASMFunction function;
        if (asmModule.getFunction(inst.getFunction().getName()) != null)
            function = asmModule.getFunction(inst.getFunction().getName());
        else
            function = asmModule.getBuiltInFunction(inst.getFunction().getName());

        var params = inst.getParameters();
        // mv to a0-a8
        for (int i = 0; i < Integer.min(8, params.size()); ++i) {
            VirtualRegister paramVR = getVRFromOperand(params.get(i));
            currentBlock.appendInst(new Move(currentBlock, PhysicalRegister.argVRs.get(i), paramVR));
        }
        // st spilled params
        StackFrame stackFrame = currentFunc.getStackFrame();
        if (stackFrame.getParamAddrMap().containsKey(function)) {
            var stackPosList = stackFrame.getParamAddrMap().get(function);
            for (int i = 8; i < params.size(); ++i) {
                VirtualRegister param = getVRFromOperand(params.get(i));
                currentBlock.appendInst(new St(currentBlock, St.ByteSize.sw, param, stackPosList.get(i - 8)));
            }
        } else {
            ArrayList<StackAddr> stackAddrList = new ArrayList<>();
            for (int i = 8; i < params.size(); ++i) {
                VirtualRegister param = getVRFromOperand(params.get(i));
                StackAddr stackAddr = new StackAddr("param_" + i);
                stackAddrList.add(stackAddr);
                currentBlock.appendInst(new St(currentBlock, St.ByteSize.sw, param, stackAddr));
            }
            stackFrame.getParamAddrMap().put(function, stackAddrList);
        }
        // call
        currentBlock.appendInst(new Call(currentBlock, function));

        // mv from a0
        if (inst.hasDstReg()) {
            VirtualRegister dst = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());
            currentBlock.appendInst(new Move(currentBlock, dst, PhysicalRegister.argVRs.get(0)));
        }
    }

    @Override
    public void visit(GetElementPtrInst inst) {
        var rd = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());

        if (inst.getPointer() instanceof GlobalVar) {
            currentBlock.appendInst(new La(currentBlock, rd, asmModule.getGlobalVar(inst.getPointer().getName())));
        } else if (inst.getIndices().size() == 1) {
            // array
            VirtualRegister pointer = currentFunc.getSymbolTable().getVR(inst.getPointer().getName());
            var index = inst.getIndices().get(0);
            if (index instanceof ConstInt) {
                int offset = ((ConstInt) index).getValue() * 4; // 4: pointer
                ASMOperand rs = getASMOperand(new ConstInt(IntType.BitWidth.i32, offset));
                if (rs instanceof Immediate)
                    currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, rd, pointer, (Immediate) rs));
                else {
                    assert rs instanceof VirtualRegister;
                    currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.add, rd, pointer, (VirtualRegister) rs));
                }
            } else {
                VirtualRegister rs1 = currentFunc.getSymbolTable().getVR(index.getName());
                VirtualRegister rs2 = new VirtualRegister("slli");
                currentFunc.getSymbolTable().addVR(rs2);
                currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.slli, rs2, rs1, new IntImm(2)));
                currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.add, rd, pointer, rs2));
            }
        } else {
            // class
            if (inst.getPointer() instanceof ConstNull) {
                currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, rd, PhysicalRegister.zeroVR,
                        new IntImm(((ConstInt) inst.getIndices().get(1)).getValue())));
            } else {
                VirtualRegister pointer = currentFunc.getSymbolTable().getVR(inst.getPointer().getName());
                StructType structType = (StructType) ((PointerType) inst.getPointer().getType()).getBaseType();
                int index = ((ConstInt) inst.getIndices().get(1)).getValue();
                int offset = structType.getOffset(index);

                if (structType.getMemberList().get(index) instanceof PointerType) {
                    ASMOperand rs = getASMOperand(new ConstInt(IntType.BitWidth.i32, offset));
                    if (rs instanceof Immediate) {
                        currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, rd, pointer, (Immediate) rs));
                    } else {
                        currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.add, rd, pointer, (VirtualRegister) rs));
                    }
                } else
                    currentFunc.getGepAddrMap().put(rd, new BaseOffsetAddr(pointer, new IntImm(offset)));
            }
        }
    }

    @Override
    public void visit(IcmpInst inst) {
        if (inst.onlyHasOneBranch()) return; // handled in BrInst

        var type = inst.getLhs().getType();
        var lhs = inst.getLhs();
        var rhs = inst.getRhs();
        var rd = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());
        if (type instanceof IntType) {
            var rs1 = getVRFromOperand(lhs);
            if (rhs instanceof Constant) {
                inst.removeE();
                // cannot have "sgti" staff: just use VR
                int value = rhs instanceof ConstBool? (((ConstBool) rhs).getValue()? 1: 0) : ((ConstInt) rhs).getValue();
                switch (inst.getOperator()) {
                    case slt -> {
                        var rs2_imm = getASMOperand(rhs);
                        if (rs2_imm instanceof VirtualRegister)
                            currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.slt, rd, rs1,
                                    (VirtualRegister) rs2_imm));
                        else if (value == 0) {
                            currentBlock.appendInst(new Unary(currentBlock, Unary.Operator.sltz, rd, rs1));
                        } else
                            currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.slti, rd, rs1,
                                    (Immediate) rs2_imm));
                    }
                    case sgt -> {
                        if (value == 0)
                            currentBlock.appendInst(new Unary(currentBlock, Unary.Operator.sgtz, rd, rs1));
                        else {
                            var rs2 = getVRFromOperand(rhs);
                            currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.slt, rd, rs2, rs1));
                        }
                    }
                    case eq, ne -> {
                        var rs2_imm_ = getASMOperand(rhs);
                        var xorTmp = new VirtualRegister("xor");
                        currentFunc.getSymbolTable().addVR(xorTmp);
                        if (rs2_imm_ instanceof VirtualRegister) {
                            currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.xor, xorTmp, rs1,
                                    (VirtualRegister) rs2_imm_));
                            currentBlock.appendInst(new Unary(currentBlock,
                                    inst.getOperator() == eq ? Unary.Operator.seqz : Unary.Operator.snez, rd, xorTmp));
                        } else if (value == 0) {
                            currentBlock.appendInst(new Unary(currentBlock,
                                    inst.getOperator() == eq ? Unary.Operator.seqz : Unary.Operator.snez, rd, rs1));
                        } else {
                                currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.xori, xorTmp, rs1,
                                        (Immediate) rs2_imm_));
                                currentBlock.appendInst(new Unary(currentBlock,
                                        inst.getOperator() == eq ? Unary.Operator.seqz : Unary.Operator.snez, rd, xorTmp));
                        }
                    }
                    default -> throw new RuntimeException();
                }
            } else {
                // rhs is not const
                var rs2 = currentFunc.getSymbolTable().getVR(rhs.getName());
                switch (inst.getOperator()) {
                    case slt -> {
                        currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.slt, rd, rs1, rs2));
                    }
                    case sgt -> {
                        currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.slt, rd, rs2, rs1));
                    }
                    case sle -> {
                        var sltTmp = new VirtualRegister("slt");
                        currentFunc.getSymbolTable().addVR(sltTmp);
                        currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.slt, sltTmp, rs2, rs1));
                        currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.xori, rd, sltTmp, new IntImm(1)));
                    }
                    case sge -> {
                        var sltTmp = new VirtualRegister("sge");
                        currentFunc.getSymbolTable().addVR(sltTmp);
                        currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.slt, sltTmp, rs1, rs2));
                        currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.xori, rd, sltTmp, new IntImm(1)));
                    }
                    case eq, ne -> {
                        var xorTmp = new VirtualRegister("xor");
                        currentFunc.getSymbolTable().addVR(xorTmp);
                        currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.xor, xorTmp, rs1, rs2));
                        currentBlock.appendInst(new Unary(currentBlock,
                                inst.getOperator() == eq ? Unary.Operator.seqz : Unary.Operator.snez, rd, xorTmp));
                    }
                    default -> throw new RuntimeException();
                }
            }
        } else {
            assert type instanceof PointerType;
            var rs1 = currentFunc.getSymbolTable().getVR(lhs.getName());
            if (rhs instanceof Constant) {
                assert rhs instanceof ConstNull;
                switch (inst.getOperator()) {
                    case eq -> currentBlock.appendInst(new Unary(currentBlock, Unary.Operator.seqz, rd, rs1));
                    case ne -> currentBlock.appendInst(new Unary(currentBlock, Unary.Operator.snez, rd, rs1));
                    default -> throw new RuntimeException();
                }
            } else {
                var rs2 = currentFunc.getSymbolTable().getVR(rhs.getName());
                VirtualRegister xorTmp = new VirtualRegister("xor");
                currentFunc.getSymbolTable().addVR(xorTmp);
                currentBlock.appendInst(new RBinary(currentBlock, RBinary.Operator.xor, xorTmp, rs1, rs2));
                switch (inst.getOperator()) {
                    case eq -> currentBlock.appendInst(new Unary(currentBlock, Unary.Operator.seqz, rd, xorTmp));
                    case ne -> currentBlock.appendInst(new Unary(currentBlock, Unary.Operator.snez, rd, xorTmp));
                    default -> throw new RuntimeException();
                }
            }
        }
    }

    @Override
    public void visit(LoadInst inst) {
        var rd = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());
        Ld.ByteSize byteSize = switch (inst.getType().getBytes()) {
            case 1 -> Ld.ByteSize.lb;
            default -> Ld.ByteSize.lw;
        };

        if (inst.getPointer() instanceof GlobalVar) {
            // lui + addi
            var globalVar = asmModule.getGlobalVar(inst.getPointer().getName());
            VirtualRegister lui = new VirtualRegister("lui");
            currentFunc.getSymbolTable().addVR(lui);
            currentBlock.appendInst(new Lui(currentBlock, lui, new RelocationImm(RelocationImm.ReloType.hi, globalVar)));
            currentBlock.appendInst(new Ld(currentBlock, byteSize, rd,
                    new BaseOffsetAddr(lui, new RelocationImm(RelocationImm.ReloType.lo, globalVar))));
        } else if (inst.getPointer() instanceof ConstNull) {
            currentBlock.appendInst(new Ld(currentBlock, byteSize, rd,
                    new BaseOffsetAddr(PhysicalRegister.zeroVR, new IntImm(0))));
        } else {
            assert inst.getPointer() instanceof Parameter || inst.getPointer() instanceof Register;
            var pointer = currentFunc.getSymbolTable().getVR(inst.getPointer().getName());
            if (currentFunc.getGepAddrMap().containsKey(pointer)) {
                BaseOffsetAddr addr = currentFunc.getGepAddrMap().get(pointer);
                currentBlock.appendInst(new Ld(currentBlock, byteSize, rd, addr));
                // ----------FIXME
//                currentBlock.appendInst(new Ld(currentBlock, byteSize, rd, new BaseOffsetAddr(addr.getBase(), addr.getOffset())));
            } else {
                currentBlock.appendInst(new Ld(currentBlock, byteSize, rd, new BaseOffsetAddr(pointer, new IntImm(0))));
            }
        }
    }

    @Override
    public void visit(PhiInst inst) {
    }

    @Override
    public void visit(RetInst inst) {
        if (inst.getRetValue() != null) {
            VirtualRegister retval = getVRFromOperand(inst.getRetValue());
            currentBlock.appendInst(new Move(currentBlock, PhysicalRegister.argVRs.get(0), retval));
        }

        PhysicalRegister.calleeSaveVRs.forEach(vr -> {
            VirtualRegister savedVR = currentFunc.getSymbolTable().getVR(vr.getName() + "_save");
            currentBlock.appendInst(new Move(currentBlock, vr, savedVR));
        });

        VirtualRegister savedRA = currentFunc.getSymbolTable().getVR(PhysicalRegister.raVR.getName() + "_save");
        currentBlock.appendInst(new Move(currentBlock, PhysicalRegister.raVR, savedRA));

        currentBlock.appendInst(new Ret(currentBlock));
    }

    @Override
    public void visit(StoreInst inst) {
        var value = getVRFromOperand(inst.getValue());
        St.ByteSize byteSize = switch (inst.getValue().getType().getBytes()) {
            case 1 -> St.ByteSize.sb;
            default -> St.ByteSize.sw;
        };

        if (inst.getPointer() instanceof GlobalVar) {
            // lui + addi
            var globalVar = asmModule.getGlobalVar(inst.getPointer().getName());
            VirtualRegister lui = new VirtualRegister("lui");
            currentFunc.getSymbolTable().addVR(lui);
            currentBlock.appendInst(new Lui(currentBlock, lui, new RelocationImm(RelocationImm.ReloType.hi, globalVar)));
            currentBlock.appendInst(new St(currentBlock, byteSize, value,
                    new BaseOffsetAddr(lui, new RelocationImm(RelocationImm.ReloType.lo, globalVar))));
        } else if (inst.getPointer() instanceof ConstNull) {
            currentBlock.appendInst(new St(currentBlock, byteSize, value,
                    new BaseOffsetAddr(PhysicalRegister.zeroVR, new IntImm(0))));
        } else {
            assert inst.getPointer() instanceof Parameter || inst.getPointer() instanceof Register;
            var pointer = currentFunc.getSymbolTable().getVR(inst.getPointer().getName());
            if (currentFunc.getGepAddrMap().containsKey(pointer)) {
                BaseOffsetAddr addr = currentFunc.getGepAddrMap().get(pointer);
                currentBlock.appendInst(new St(currentBlock, byteSize, value, addr));
                // ---------FIXME
//                currentBlock.appendInst(new St(currentBlock, byteSize, value, new BaseOffsetAddr(addr.getBase(), addr.getOffset())));
                // ---------
            } else {
                currentBlock.appendInst(new St(currentBlock, byteSize, value, new BaseOffsetAddr(pointer, new IntImm(0))));
            }
        }
    }

    @Override
    public void visit(MoveInst inst) {
        var dstReg = currentFunc.getSymbolTable().getVR(inst.getDstReg().getName());
        var src = getASMOperand(inst.getSrc());
        if (src instanceof Immediate) {
            currentBlock.appendInst(new IBinary(currentBlock, IBinary.Operator.addi, dstReg, PhysicalRegister.zeroVR,
                    (Immediate) src));
        } else {
            assert src instanceof VirtualRegister;
            currentBlock.appendInst(new Move(currentBlock, dstReg, (VirtualRegister) src));
        }
    }

    @Override
    public void visit(ConstInt operand) {

    }

    @Override
    public void visit(ConstBool operand) {

    }

    @Override
    public void visit(ConstNull operand) {

    }

    @Override
    public void visit(ConstString operand) {

    }

    @Override
    public void visit(GlobalVar operand) {

    }

    @Override
    public void visit(Parameter operand) {

    }

    @Override
    public void visit(Register operand) {

    }

    @Override
    public void visit(ArrayType type) {

    }

    @Override
    public void visit(IntType type) {

    }

    @Override
    public void visit(PointerType type) {

    }

    @Override
    public void visit(StructType type) {

    }

    @Override
    public void visit(VoidType type) {

    }
}
