package ir;

import ir.instruction.*;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.PointerType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ResolveAlloca extends IRPass {
    public ResolveAlloca(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        for (Function function : module.getFuncMap().values()) {
            runFunc(function);
        }
        return false;
    }

    @Override
    protected void runFunc(Function function) {
        LinkedHashMap<Block, LinkedHashSet<LoadInst>> allocLoads = new LinkedHashMap<>();
        LinkedHashMap<Block, LinkedHashMap<Register, Operand>> allocStoreMap = new LinkedHashMap<>(); // store (2) to (1)
        LinkedHashMap<Operand, Operand> replaceOps = new LinkedHashMap<>();
        LinkedHashSet<Block> defBlocks = new LinkedHashSet<>();
        LinkedHashMap<Block, LinkedHashMap<Register, PhiInst>> allocPhiMap = new LinkedHashMap<>();

        function.getBlocks().forEach(block -> {
            allocLoads.put(block, new LinkedHashSet<>());
            allocStoreMap.put(block, new LinkedHashMap<>());
            allocPhiMap.put(block, new LinkedHashMap<>());
        });

        var allocRegs = function.getAllocRegs();

        for (Block block : function.getBlocks()) {
            var storeMap = allocStoreMap.get(block);
            var loads = allocLoads.get(block);
            var inst = block.getHeadInst();
            while (inst != null) {
                var next = inst.next;
                if (inst instanceof LoadInst) {
                    var addr = ((LoadInst) inst).getPointer();
                    assert addr.getType() instanceof PointerType;
                    if (addr instanceof Register && allocRegs.contains(addr)) {
                        if (storeMap.containsKey(addr)) {
                            replaceOps.put(inst.getDstReg(), storeMap.get(addr));
                            inst.removeFromBlock(); // remove inst
                        } else
                            loads.add((LoadInst) inst);
                    }
                } else if (inst instanceof StoreInst) {
                    var addr = ((StoreInst) inst).getPointer();
                    if (addr instanceof Register && allocRegs.contains(addr)) {
                        defBlocks.add(block);
                        allocStoreMap.get(block).put((Register) addr, ((StoreInst) inst).getValue());
                        inst.removeFromBlock(); // remove inst
                    }
                }
                inst = next;
            }
        }

//        LinkedHashSet<Block> F = new LinkedHashSet<>();
//        LinkedHashSet<Block> W = new LinkedHashSet<>();
//
//        for (Register v : allocRegs) {
//            F.clear();
//            W.clear();
//            var defs_v = defBlocks.get(v);
//            String phiName = v.getName().replace(".addr", ".phi");
//            assert v.getType() instanceof PointerType;
//            var type = ((PointerType) v.getType()).getBaseType();
//            for (Block block : defs_v) {
//                W.add(block);
//            }
//            while (!W.isEmpty()) {
//                var X = W.iterator().next();
//                W.remove(X);
//                for (Block Y : X.getDomFrontier()) {
//                    if (!F.contains(Y)) {
//                        // add Phi at front
//                        Register phiReg = new Register(type, phiName);
//                        PhiInst newPhi = new PhiInst(Y, new ArrayList<>(), new ArrayList<>(), phiReg);
//                        allocPhiMap.get(Y).put(v, newPhi);
//                        Y.getHeadInst().addPrev(newPhi);
//                        F.add(Y);
//                        if (!defs_v.contains(Y))
//                            W.add(Y);
//                        if (!allocStoreMap.get(Y).containsKey(v))
//                            allocStoreMap.get(Y).put(v, phiReg);
//                    }
//                }
//            }
//        }

        LinkedHashSet<Block> runningSet;
        int cnt = 0;
        while (!defBlocks.isEmpty()) {
            runningSet = defBlocks;
            defBlocks = new LinkedHashSet<>();
            for (Block runner : runningSet) {
                var runnerDefAlloc = allocStoreMap.get(runner);
                if (!runnerDefAlloc.isEmpty() && !runner.getDomFrontier().isEmpty()) {
                    for (Block df : runner.getDomFrontier()) {
                        for (Map.Entry<Register, Operand> entry : runnerDefAlloc.entrySet()) {
                            var allocVar = entry.getKey();
                            var value = entry.getValue();
                            if (!allocPhiMap.get(df).containsKey(allocVar)) {
                                String phiName = allocVar.getName().replace(".addr", "." + cnt++);
                                assert allocVar.getType() instanceof PointerType;
                                Register phiReg = new Register(((PointerType) allocVar.getType()).getBaseType(), phiName, function);
                                PhiInst newPhi = new PhiInst(df, new ArrayList<>(), new ArrayList<>(), phiReg);
                                df.getHeadInst().addPrev(newPhi);
                                if (!allocStoreMap.get(df).containsKey(allocVar)) {
                                    allocStoreMap.get(df).put(allocVar, phiReg);
                                    defBlocks.add(df);
                                }
                                allocPhiMap.get(df).put(allocVar, newPhi);
                            }
                        }
                    }
                }
            }
        }

        // renaming
        for (Block block : function.getBlocks()) {
            if (!allocPhiMap.get(block).isEmpty()) {
                // add origin to PhiInsts
                for (Map.Entry<Register, PhiInst> entry : allocPhiMap.get(block).entrySet()) {
                    Register addr = entry.getKey();
                    PhiInst phi = entry.getValue();
                    for (Block pred : block.getPredecessors()) {
                        Block runner = pred;
                        while (!allocStoreMap.get(runner).containsKey(addr))
                            runner = runner.getiDom();
                        phi.addOrgin(allocStoreMap.get(runner).get(addr), pred);
                    }
                }
            }
            if (!allocLoads.get(block).isEmpty()) {
                allocLoads.get(block).forEach(load -> {
                    var dst = load.getDstReg();
                    assert load.getPointer() instanceof Register;
                    Register toBeReplaced = (Register) load.getPointer();
                    Operand replaced;
                    if (allocPhiMap.get(block).containsKey(toBeReplaced))
                        replaced = allocPhiMap.get(block).get(toBeReplaced).getDstReg();
                    else {
                        var tmp = block.getiDom();
                        while (true) {
                            if (allocStoreMap.get(tmp).containsKey(toBeReplaced)) {
                                replaced = allocStoreMap.get(tmp).get(toBeReplaced);
                                break;
                            } else
                                tmp = tmp.getiDom();
                        }
                    }
                    replaceOps.put(dst, finalReplace(replaceOps, replaced));
                    load.removeFromBlock();
                });
            }
        }

        for (Map.Entry<Operand, Operand> entry : replaceOps.entrySet()) {
            Operand origin = entry.getKey(); Operand replaced = entry.getValue();
            ((Register) origin).replaceAllUseWith(finalReplace(replaceOps, replaced));
        }

        // clean AllocaInst && useless LoadInsts. These are constructed when only using left value.
        function.getBlocks().forEach(block -> {
            for (var inst = block.getHeadInst(); inst != null;) {
                var next = inst.next;
                if (inst instanceof AllocaInst || (inst instanceof LoadInst && inst.getDstReg().getUse().isEmpty()))
                    inst.removeFromBlock();
                inst = next;
            }
        });
    }

    private Operand finalReplace(LinkedHashMap<Operand, Operand> replaceOps, Operand replaceOp) {
        // find the final one: TODO: change this to Disjoint-set
        var ret = replaceOp;
        while (replaceOps.containsKey(ret))
            ret = replaceOps.get(ret);
        return ret;
    }
}
