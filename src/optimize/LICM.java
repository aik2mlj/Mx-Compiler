package optimize;

import ir.*;
import ir.Module;
import ir.instruction.*;
import ir.operand.Constant;
import ir.operand.Operand;
import ir.operand.Parameter;
import ir.operand.Register;

import java.util.LinkedHashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

public class LICM extends IRPass {
    private LoopTreeConstructor loopTreeConstructor;
    LinkedHashSet<Inst> loopInvariants = new LinkedHashSet<>();
    Queue<Operand> workList = new LinkedList<>();

    public LICM(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        module.getFuncMap().values().forEach(function -> {
            if (function.getName().equals("main")) runFunc(function);
        });
        return changed;
    }

    @Override
    protected void runFunc(Function function) {
        loopTreeConstructor = new LoopTreeConstructor(function, true);
        loopTreeConstructor.runFunc();
        new Dominancer(module).runFunc(function); // rerun dominancer: loopTreeConstructor has introduce some preHeader blocks.
        for (Loop globalLoop : loopTreeConstructor.globalLoops) {
            visit(globalLoop);
        }
    }

    private void visit(Loop loop) {
        loopInvariants.clear();
        workList.clear();
        detectLoopInvariants(loop);
        hoistLoopInvariants(loop);
        loop.getChildLoops().forEach(this::visit);
    }

    private void hoistLoopInvariants(Loop loop) {
        for (Inst liInst : loopInvariants) {
            liInst.removeFromBlock();
            loop.appendInstToPreHeader(liInst);
            changed = true;
        }
    }

    private boolean dominatesAllTails(Block block, Loop loop) {
        for (Block tail : loop.getTails()) {
            if (!block.dominates(tail))
                return false;
        }
        return true;
    }

    private void detectLoopInvariants(Loop loop) {
        // get constants and outsideLoops
        for (Block block : loop.getLoopBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (inst.hasDstReg() && allDefsReachingAreOutsideLoopOrConstOrLI(inst, loop)) {
                    workList.add(inst.getDstReg());
                    loopInvariants.add(inst);
                }
            }
        }
        // find all loop invariants
        while (!workList.isEmpty()) {
            var invariant = workList.poll();
            for (Inst useInst : invariant.getUse().keySet()) {
                if (!loopInvariants.contains(useInst) && useInst.hasDstReg() &&
                        allDefsReachingAreOutsideLoopOrConstOrLI(useInst, loop)) {
                    workList.add(useInst.getDstReg());
                    loopInvariants.add(useInst);
                }
            }
        }
    }

    private boolean allDefsReachingAreOutsideLoopOrConstOrLI(Inst inst, Loop loop) {
        for (Operand use : inst.getUses()) {
            if (!(use instanceof Parameter) && !(use instanceof Constant) &&
                    (!(use instanceof Register) ||
                            (loop.getLoopBlocks().contains(((Register) use).getDefInst().getParentBlock()) &&
                                    !loopInvariants.contains(((Register) use).getDefInst()))))
                return false;
        }
        if (!dominatesAllTails(inst.getParentBlock(), loop)) { // not dominates all the tails of this loop: cannot be hoisted
            return false;
        }
        if (inst instanceof PhiInst) {
            for (Block pred : ((PhiInst) inst).getPredecessors()) {
                if (!dominatesAllTails(pred, loop)) // if a pred does not dominate loop tails, this PhiInst is not LI.
                    return false;
            }
        } else if (inst instanceof CallInst) {
//            return !module.getIOBuiltInFunc().contains(((CallInst) inst).getFunction()) && !((CallInst) inst).getFunction().hasSideEffect();
            return false; //FIXME
        } else if (inst instanceof LoadInst || inst instanceof GetElementPtrInst) {
            // hoisting GEP is useless(since load offset depends on it) and may cause fault.
            return false; // FIXME
        }
        return true;
    }
}
