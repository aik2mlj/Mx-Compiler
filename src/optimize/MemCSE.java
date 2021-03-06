package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.CallInst;
import ir.instruction.LoadInst;
import ir.instruction.StoreInst;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class MemCSE extends IRPass {
    public MemCSE(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        new SillyEffectChecker(module).run();
        module.getFuncMap().values().forEach(this::runFunc);
        return changed;
    }

    @Override
    protected void runFunc(Function function) {
        for (Block block : function.getBlocks()) {
            LinkedHashMap<Operand, Operand> availablePointerMap = new LinkedHashMap<>();
            // remove useless LoadInst
            for (var inst = block.getHeadInst(); inst != null; ) {
                var next = inst.next;
                if (inst instanceof LoadInst) {
                    if (!availablePointerMap.containsKey(((LoadInst) inst).getPointer())) {
                        if (inst.prev instanceof StoreInst && ((StoreInst) inst.prev).getPointer() == ((LoadInst) inst).getPointer()) {
                            // silly peephole.
                            availablePointerMap.put(((LoadInst) inst).getPointer(), ((StoreInst) inst.prev).getValue());
                            inst.getDstReg().replaceAllUseWith(((StoreInst) inst.prev).getValue());
                            inst.removeFromBlock();
                            changed = true;
                        } else
                            availablePointerMap.put(((LoadInst) inst).getPointer(), inst.getDstReg());
                    } else {
                        inst.getDstReg().replaceAllUseWith(availablePointerMap.get(((LoadInst) inst).getPointer()));
                        inst.removeFromBlock();
                        changed = true;
                    }
                } else if (inst instanceof StoreInst) {
                    availablePointerMap.remove(((StoreInst) inst).getPointer());
                } else if (inst instanceof CallInst) {
                    Function callee = ((CallInst) inst).getFunction();
                    HashSet<Operand> affectedOps = new HashSet<>(callee.getAffectedOps());
                    for (Integer index : callee.getAffectedParamIndices()) {
                        affectedOps.add(((CallInst) inst).getParameters().get(index));
                    }
                    affectedOps.forEach(availablePointerMap::remove);
                }
                inst = next;
            }

            // remove useless StoreInst
            // backward: find storeInst that are (not loaded afterward) && (already has a same storeInst afterward).
            HashSet<Operand> storedSafely = new HashSet<>();
            for (var inst = block.getTailInst(); inst != null;) {
                var prev = inst.prev;
                if (inst instanceof StoreInst) {
                    if (storedSafely.contains(((StoreInst) inst).getPointer())) {
                        inst.removeFromBlock();
                        changed = true;
                    } else {
                        storedSafely.add(((StoreInst) inst).getPointer());
                    }
                } else if (inst instanceof LoadInst) {
                    storedSafely.remove(((LoadInst) inst).getPointer());
                } else if (inst instanceof CallInst) {
//                    Function callee = ((CallInst) inst).getFunction();
//                    HashSet<Operand> loadedOps = new HashSet<>(callee.getLoadedOps());
//                    for (Integer index : callee.getLoadedParamIndices()) {
//                        loadedOps.add(((CallInst) inst).getParameters().get(index));
//                    }
//                    loadedOps.forEach(storedSafely::remove);
                    // FIXME:
                    storedSafely.clear();
                }

                inst = prev;
            }
        }
    }
}
