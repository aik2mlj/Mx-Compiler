package backend;

import riscv.*;
import riscv.instuctions.*;
import riscv.operands.*;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

public class RegAllocator_origin {
    private ASMModule module;

    public RegAllocator_origin(ASMModule module) {
        this.module = module;
    }

    public void run() {
        for (ASMFunction function : module.getFuncMap().values()) {
//            if (!function.getName().equals("__init__"))
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
//        Map<VirtualRegister, Integer> beginPlace = new LinkedHashMap<>();
//        Map<VirtualRegister, Integer> endPlace = new LinkedHashMap<>();
//        Set<VirtualRegister> spilledVRs = new LinkedHashSet<>();
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
        int move = 1;
        for (ASMBlock block : function.getBlocks()) {
            ASMInst inst = block.getHeadInst();
            while (inst != null) {
//                if (!(inst instanceof Ld) && !(inst instanceof St)) {
                int tmpNum = 0;
                move = 1;
                if (inst.getUses() != null) {
                    for (VirtualRegister use : inst.getUses())
                        if (!use.isAollocated()) {
                            StackAddr stackAddr = stackFrame.getSpillAddrMap().get(use);
                            if (use.getColor() != null && use.getColor().emit().charAt(0) != 't') continue;
//                        if (inst instanceof Ld || inst instanceof St) continue;
                            VirtualRegister spilledVR = new VirtualRegister(use.getName() + "_spill" + cnt);
                            function.getSymbolTable().addVRRename(spilledVR);
                            spilledVR.setColor(PhysicalRegister.prs.get("t" + tmpNum));
                            tmpNum++;
                            inst.replaceUse(use, spilledVR);

                            Ld newLd = new Ld(block, Ld.ByteSize.lw, spilledVR, stackAddr);
                            newLd.setFake();
                            inst.addPrev(newLd);
                            ++cnt;
                        }
                }
                if (inst.getDefs() != null) {
                    for (VirtualRegister def : inst.getDefs())
                        if (!def.isAollocated()) {
                            StackAddr stackAddr = stackFrame.getSpillAddrMap().get(def);
                            if (def.getColor() != null && def.getColor().emit().charAt(0) != 't') continue;
//                        if (inst instanceof Ld || inst instanceof St) continue;
                            VirtualRegister spilledVR = new VirtualRegister(def.getName() + "_spill" + cnt);
                            function.getSymbolTable().addVRRename(spilledVR);
                            spilledVR.setColor(PhysicalRegister.prs.get("t" + tmpNum));
                            tmpNum++;
                            inst.replaceDef(def, spilledVR);

                            St newSt = new St(block, St.ByteSize.sw, spilledVR, stackAddr);
                            newSt.setFake();
                            inst.addNext(newSt);
                            ++move;
                            ++cnt;
                        }
                }
                for (; move > 0; --move) {
                    inst = inst.next;
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
            if (vr.getColor() == null)
                System.err.println("un: " + vr.getName());
            else
                System.err.println(":" + vr.getName());
        }
    }

    private void LoadStoreResolve(ASMFunction function) {
        for (ASMBlock block : function.getBlocks()) {
            for (ASMInst inst = block.getHeadInst(); inst != null; inst = inst.next) {
                BaseOffsetAddr addr;
                if (inst instanceof Ld && ((Ld) inst).getAddr() instanceof BaseOffsetAddr)
                    addr = (BaseOffsetAddr) ((Ld) inst).getAddr();
                else if (inst instanceof St && ((St) inst).getAddr() instanceof BaseOffsetAddr)
                    addr = (BaseOffsetAddr) ((St) inst).getAddr();
                else
                    continue;
                if (addr.getBase().getColor() != null) {
//                    System.err.println("resolve not: " + inst.emit());
                    continue;
                }
                if (addr.getOffset() instanceof RelocationImm) {
                    continue;
                }
                if (addr.getBase().isAollocated()) {
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
    }

    private void moveStackPointer(ASMFunction function) {
        int frameSize = function.getStackFrame().getSize();
        if (frameSize == 0) return;

        VirtualRegister sp = PhysicalRegister.vrs.get("sp");
        function.getEntryBlock().pushFrontInst(new IBinary(function.getEntryBlock(), IBinary.Operator.addi,
                sp, sp, new IntImm(-4 * frameSize)));
        for (ASMBlock block : function.getBlocks()) {
            if (block.getTailInst() instanceof Ret) {
                block.getTailInst().addPrev(new IBinary(block,
                        IBinary.Operator.addi, sp, sp, new IntImm(frameSize * 4)));
                break;
            }
        }
    }
}
