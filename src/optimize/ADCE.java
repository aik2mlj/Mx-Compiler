package optimize;

import ir.*;
import ir.Module;
import ir.instruction.*;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.PointerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

// Engineering a Compiler P545
public class ADCE extends IRPass {
    private HashMap<Function, HashSet<Inst>> workListMap = new HashMap<>();
    private HashMap<Function, HashSet<CallInst>> calledByMap = new HashMap<>();
    private LinkedHashSet<Function> sideEffectWorkList = new LinkedHashSet<>();

    public ADCE(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        workListMap.clear();
        calledByMap.clear();
        module.getFuncMap().values().forEach(function -> workListMap.put(function, new HashSet<>()));
        module.getFuncMap().values().forEach(function -> calledByMap.put(function, new HashSet<>()));
        module.getBuiltInFuncMap().values().forEach(function -> calledByMap.put(function, new HashSet<>()));

        module.getFuncMap().values().forEach(this::getWorkList);
        resolveCalls();
        module.getFuncMap().values().forEach(this::runFunc);
        calledByMap.forEach((function, callInsts) -> {
            if (callInsts.isEmpty()) module.getFuncMap().remove(function); // clean useless function.
        });
        return changed;
    }

    private void resolveCalls() {
        // post traverse calls to deliver side effect.
        HashSet<Function> visited = new HashSet<>();
        while (!sideEffectWorkList.isEmpty()) {
            var func = sideEffectWorkList.iterator().next();
            visited.add(func);
            sideEffectWorkList.remove(func);
            calledByMap.get(func).forEach(callerInst -> {
                var caller = callerInst.getParentBlock().getParentFunc();
                callerInst.setLive(true);
                workListMap.get(caller).add(callerInst);
                caller.setSideEffect(true);
                if (!visited.contains(caller))
                    sideEffectWorkList.add(caller);
            });
        }
    }

    @Override
    protected void runFunc(Function function) {
        markInsts(function);
//        new IRPrinter("Optcout.ll", module);
        sweepInsts(function);
    }

    private void getWorkList(Function function) {
        HashSet<Operand> outerOps = new HashSet<>(module.getGlobalVarMap().values()); // outside values that may change
        function.getParameters().forEach(parameter -> {
            if (parameter.getType() instanceof PointerType) // if a param is a pointer, it will influence outside as well.
                outerOps.add(parameter);
        });
        HashSet<Inst> workList = workListMap.get(function);
        function.setSideEffect(false);
        for (Block block : function.getBlocks()) {
            block.setLive(false);
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (isCritical(inst, outerOps)) {
                    inst.setLive(true);
                    block.setLive(true);
                    if (!(inst instanceof RetInst)) {
                        sideEffectWorkList.add(function);
                        function.setSideEffect(true); // also check side effect here, these are originally side effective.
                    }
                    workList.add(inst);
                } else
                    inst.setLive(false);
                if (inst instanceof CallInst) { // collect all the CallInsts.
                    calledByMap.get(((CallInst) inst).getFunction()).add((CallInst) inst);
                }
            }
        }
    }

    private void markInsts(Function function) {

        var workList = workListMap.get(function);

        while (!workList.isEmpty()) {
            var inst = workList.iterator().next();
            workList.remove(inst);

            inst.getUses().forEach(use -> {
                if (use instanceof Register) {
                    var defInst = ((Register) use).getDefInst();
                    if (!defInst.isLive()) {
                        defInst.setLive(true);
                        defInst.getParentBlock().setLive(true);
                        workList.add(defInst);
                    }
                }
            });

            if (inst instanceof PhiInst) {
                for (Block pred : ((PhiInst) inst).getPredecessors()) {
                    var tailInst = pred.getTailInst();
                    if (tailInst instanceof BrInst && !tailInst.isLive()) {
                        tailInst.setLive(true);
                        tailInst.getParentBlock().setLive(true);
                        workList.add(tailInst);
                    }
                }
            }

            for (Block rdf : inst.getParentBlock().getReverseDomFrontier()) {
                var tailInst = rdf.getTailInst();
                if (!(tailInst instanceof TerminalInst)) throw new RuntimeException();
                if (tailInst instanceof BrInst && !tailInst.isLive()) {
                    tailInst.setLive(true);
                    tailInst.getParentBlock().setLive(true);
                    workList.add(tailInst);
                }
            }
        }
    }

    private boolean isCritical(Inst inst, HashSet<Operand> outerOps) {
        if (inst instanceof RetInst ||
                inst instanceof CallInst && module.getIOBuiltInFunc().contains(((CallInst) inst).getFunction()) ||
                inst.hasDstReg() && outerOps.contains(inst.getDstReg()) ||
                inst instanceof LoadInst &&
                        (outerOps.contains(((LoadInst) inst).getPointer()) || ((Register) ((LoadInst) inst).getPointer()).getDefInst() instanceof GetElementPtrInst) ||
                inst instanceof StoreInst)
            return true;
//        if (inst instanceof GetElementPtrInst) {
//            inst.getUses().retainAll(outerOps);
//            if (!inst.getUses().isEmpty()) return true;
//        }
        return false;
    }

    private void sweepInsts(Function function) {
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null;) {
                var next = inst.next;
                if (!inst.isLive()) {
                    if (inst instanceof BrInst && ((BrInst) inst).getCondition() != null) {
//                        System.err.println(inst);
                        // find the nearest live post dominator
                        var nearestLivePostDom = block.getReverseIDom();
                        while (!nearestLivePostDom.isLive())
                            nearestLivePostDom = nearestLivePostDom.getReverseIDom();
                        block.replaceBrInst(new BrInst(block, null, nearestLivePostDom, null));
                        changed = true;
                    }
                    if (!(inst instanceof BrInst)) {
                        inst.removeFromBlock();
                        if (inst instanceof CallInst)
                            calledByMap.get(((CallInst) inst).getFunction()).remove(inst);
                        changed = true;
                    }
                }
                inst = next;
            }
        }
    }
}
