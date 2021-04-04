package backend;

import riscv.*;
import riscv.instuctions.*;
import riscv.operands.*;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegAllocator {
    private ASMModule module;

    public RegAllocator(ASMModule module) {
        this.module = module;
    }

    public void run() {
        for (ASMFunction function : module.getFuncMap().values()) {
            runFunc(function);
        }
    }

    private void runFunc(ASMFunction function) {
        int spillCnt = 0;
        StackFrame stackFrame = function.getStackFrame();
        for (VirtualRegister vr : function.getSymbolTable().getVrMap().values()) {
//            System.err.println(vr.getName());
            StackAddr stackAddr = new StackAddr(vr.getName());
            stackFrame.getSpillAddrMap().put(vr, stackAddr);
        }
//        Map<VirtualRegister, Integer> beginPlace = new HashMap<>();
//        Map<VirtualRegister, Integer> endPlace = new HashMap<>();
//        Set<VirtualRegister> spilledVRs = new HashSet<>();
//        int tmpPRPtr = 0;
//        int ptr = 0;
//        for (ASMBlock block : function.getBlocks()) {
//            for (var inst = block.getInsts().getFirst(); block.getInsts().indexOf(inst) < block.getInsts().size() - 1;
//                 inst = block.getInsts().get(block.getInsts().indexOf(inst) + 1)) {
//                for (VirtualRegister def: inst.getDefs()) {
//                    if (!beginPlace.containsKey(def)) {
//                        beginPlace.put(def, ptr);
//                        if (tmpPRPtr < PhysicalRegister.tmpPRs.size()) {
//                            def.setTrueReg(PhysicalRegister.tmpPRs.get(tmpPRPtr++));
//                        } else {
//                            spilledVRs.add(def);
//                        }
//                    }
//                    if (!endPlace.containsKey(def)) {
//                        endPlace.put(def, ptr);
//                    } else
//                        endPlace.replace(def, ptr);
//                }
////                for (VirtualRegister use: inst.getUses()) {
////                    if (beginPlace.containsKey(use)) {
////                        assert use.getTrueReg() != null || stackFrame.getSpillAddrMap().containsKey(use);
////                    } else {
////                        beginPlace.put(use, ptr);
////                    }
////
////                    if (endPlace.containsKey(use))
////                        endPlace.replace(use, ptr);
////                    else
////                        endPlace.put(use, ptr);
////                }
//                ++ptr;
//            }
//        }
        int cnt = 0;
        int move;
        for (ASMBlock block : function.getBlocks()) {
            for (var inst = block.getInsts().getFirst(); block.getInsts().indexOf(inst) < block.getInsts().size() - 1;
                 inst = block.getInsts().get(block.getInsts().indexOf(inst) + move)) {
//                var inst = block.getInsts().get(p);
//                System.err.println(inst.emit());
//                if (!(inst instanceof Ld) && !(inst instanceof St)) {
                int i = 0;
                move = 1;
                if (inst.getUses() != null) {
                    for (VirtualRegister use : inst.getUses()) {
//                        System.err.println(use.getName());
                        StackAddr stackAddr = stackFrame.getSpillAddrMap().get(use);
                        if (use.getTrueReg() != null && use.getTrueReg().emit().charAt(0) != 't') continue;
//                        if (inst instanceof Ld || inst instanceof St) continue;
                        VirtualRegister spilledVR = new VirtualRegister(use.getName() + "_spill" + cnt);
                        function.getSymbolTable().addVR(spilledVR);
                        spilledVR.setTrueReg(PhysicalRegister.prs.get("t" + i));
                        i++;
                        inst.replaceUse(use, spilledVR);
                        block.addInstBefore(inst, new Ld(block, Ld.ByteSize.lw, spilledVR, stackAddr));
                        ++cnt;
                    }
                }
                if (inst.getDefs() != null) {
                    for (VirtualRegister def : inst.getDefs()) {
                        StackAddr stackAddr = stackFrame.getSpillAddrMap().get(def);
                        if (def.getTrueReg() != null && def.getTrueReg().emit().charAt(0) != 't') continue;
//                        if (inst instanceof Ld || inst instanceof St) continue;
                        VirtualRegister spilledVR = new VirtualRegister(def.getName() + "_spill" + cnt);
                        function.getSymbolTable().addVR(spilledVR);
                        spilledVR.setTrueReg(PhysicalRegister.prs.get("t" + i));
                        i++;
                        inst.replaceDef(def, spilledVR);
                        block.addInstAfter(inst, new St(block, St.ByteSize.sw, spilledVR, stackAddr));
                        ++move;
                        ++cnt;
                    }
                }
//                }
            }
        }
        stackFrame.getAllAddr();
        LoadStoreResolve(function);
//        checkUnColored(function);
        moveStackPointer(function);
    }

    private void checkUnColored(ASMFunction function) {
        for (VirtualRegister vr : function.getSymbolTable().getVrMap().values()) {
            if (vr.getTrueReg() == null)
                System.err.println("un: " + vr.getName());
            else
                System.err.println(":" + vr.getName());
        }
    }

    private void LoadStoreResolve(ASMFunction function) {
        for (ASMBlock block : function.getBlocks()) {
            for (var inst = block.getInsts().getFirst(); block.getInsts().indexOf(inst) < block.getInsts().size() - 1;
                 inst = block.getInsts().get(block.getInsts().indexOf(inst) + 1)) {
                BaseOffsetAddr addr;
                if (inst instanceof Ld && ((Ld) inst).getAddr() instanceof BaseOffsetAddr)
                    addr = (BaseOffsetAddr) ((Ld) inst).getAddr();
                else if (inst instanceof St && ((St) inst).getAddr() instanceof BaseOffsetAddr)
                    addr = (BaseOffsetAddr) ((St) inst).getAddr();
                else
                    continue;
                if (addr.getBase().getTrueReg() != null) {
//                    System.err.println("resolve not: " + inst.emit());
                    continue;
                }
                if (addr.getOffset() instanceof RelocationImm) {
                    continue;
                }
                StackAddr stackAddr = function.getStackFrame().getSpillAddrMap().get(addr.getBase());
                StackAddr newAddr = new StackAddr(addr.getBase().getName() + "_offset");
                assert addr.getOffset() instanceof IntImm;
                newAddr.setOffset(((IntImm) addr.getOffset()).getValue() + stackAddr.getOffset());

                if (inst instanceof Ld)
                    ((Ld) inst).setAddr(newAddr);
                else ((St) inst).setAddr(newAddr);
            }
        }
    }

    private void moveStackPointer(ASMFunction function) {
        int frameSize = function.getStackFrame().getSize();
        if (frameSize == 0) return;

        VirtualRegister sp = PhysicalRegister.vrs.get("sp");
        function.getEntryBlock().pushFrontInst(new IBinary(function.getEntryBlock(), IBinary.Operator.addi,
                sp, sp, new IntImm(-4 * frameSize)));
        for (ASMBlock block : function.getBlocks()) {
            if (block.getInsts().getLast() instanceof Ret) {
                block.addInstBefore(block.getInsts().getLast(), new IBinary(block,
                        IBinary.Operator.addi, sp, sp, new IntImm(frameSize * 4)));
                break;
            }
        }
    }
}
