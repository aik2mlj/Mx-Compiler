package optimize;

import ir.*;
import ir.Module;
import ir.instruction.CallInst;
import ir.instruction.Inst;
import ir.instruction.RetInst;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.*;

public class Inliner extends IRPass {
    private LinkedHashMap<Function, LinkedHashSet<Function>> recursiveCalleeMap = new LinkedHashMap<>();
    private LinkedHashMap<Function, Integer> instCnt = new LinkedHashMap<>();
    private static final int instCntLimit = 128;

    public Inliner(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        module.getFuncMap().values().forEach(function -> {
            recursiveCalleeMap.put(function, new LinkedHashSet<>());
            countInstsAndCalls(function);
        });
        resolveCalls();
        nonRecursiveInline();
        recursiveInline();
        return changed;
    }

    @Override
    protected void runFunc(Function function) {

    }

    private void countInstsAndCalls(Function function) {
        int cnt = 0;
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                cnt++;
                if (inst instanceof CallInst) {
                    recursiveCalleeMap.get(function).add(((CallInst) inst).getFunction());
                }
            }
        }
        instCnt.put(function, cnt);
    }

    private void resolveCalls() {
        Queue<Function> workList = new LinkedList<>();
        for (Function function : module.getFuncMap().values()) {
            workList.clear();
            var recursiveCallee = recursiveCalleeMap.get(function);
            workList.addAll(recursiveCallee);
            while (!workList.isEmpty()) {
                var callee = workList.poll();
                if (recursiveCalleeMap.get(callee) != null) {
                    for (Function callcallee : recursiveCalleeMap.get(callee)) {
                        if (!recursiveCallee.contains(callcallee)) {
                            recursiveCallee.add(callcallee);
                            workList.add(callcallee);
                        }
                    }
                }
            }
        }
    }

    private Inst inlineCallee(CallInst callInst) {
        Block startBlock = callInst.getParentBlock();
        Function function = callInst.getParentBlock().getParentFunc();
        Function callee = callInst.getFunction();
        SymbolTable symbolTable = function.getSymbolTable();

        symbolTable.clearCloneCache();
        for (Block originBlock : callee.getOrderBlocks()) {
            var clonedBlock = symbolTable.getClonedBlock(originBlock);
            function.appendBlock(clonedBlock); // here clonedBlock are also added to SymbolTable.
            clonedBlock.cloneInstsFrom(originBlock); // clone instructions in block.
        }
        // replace paramRegs with callInst 's paramValues
        for (int i = 0; i < callee.getParameters().size(); ++i) {
            var param = callee.getParameters().get(i);
            var paramReg = symbolTable.getClonedOperand(param);
            assert paramReg instanceof Register;
            Operand paramValue = callInst.getParameters().get(i);
            ((Register) paramReg).replaceAllUseWith(paramValue);
        }
        Block inlinedRetBlock = symbolTable.getClonedBlock(callee.getRetBlock());
        assert inlinedRetBlock.getTailInst() instanceof RetInst;
        if (callInst.hasDstReg()) {
            callInst.getDstReg().replaceAllUseWith(((RetInst) inlinedRetBlock.getTailInst()).getRetValue());
        }
        inlinedRetBlock.getTailInst().removeFromBlock(); // remove ret
        Block splitBlock = callInst.getParentBlock().splitBlockBy(callInst); // now startBlock has no terminal inst.
        callInst.removeFromBlock();
        startBlock.appendBrInstTo_U(symbolTable.getClonedBlock(callee.getEntryBlock()));
        inlinedRetBlock.appendBrInstTo_U(splitBlock); // change to br to splitBlock
        function.appendBlock(splitBlock);
        return splitBlock.getHeadInst();
    }

    private void nonRecursiveInline() {
        boolean change_;
        do {
            change_ = false;
            for (Function function : module.getFuncMap().values()) {
                for (Block block : function.getOrderBlocks()) {
                    for (var inst = block.getHeadInst(); inst != null;) {
                        if (inst instanceof CallInst) {
                            var callee = ((CallInst) inst).getFunction();
                            if (module.getFuncMap().containsValue(callee) &&
                                    instCnt.get(callee) < instCntLimit && callee != function && !recursiveCalleeMap.get(callee).contains(callee)) {
                                inst = inlineCallee((CallInst) inst);
                                instCnt.replace(function, instCnt.get(function) + instCnt.get(callee));
                                change_ = true;
                                changed = true;
                                continue;
                            }
                        }
                        inst = inst.next;
                    }
                }
            }
        } while (change_);
    }

    private void recursiveInline() {
        for (int i = 0; i < 3; ++i) {
            for (Function function : module.getFuncMap().values()) {
                for (Block block : function.getOrderBlocks()) {
                    for (var inst = block.getHeadInst(); inst != null;) {
                        if (inst instanceof CallInst) {
                            var callee = ((CallInst) inst).getFunction();
                            if (module.getFuncMap().containsValue(callee) &&
                                    instCnt.get(callee) < instCntLimit && callee == function) {
                                inst = inlineCallee((CallInst) inst);
                                instCnt.replace(function, instCnt.get(function) + instCnt.get(callee));
                                changed = true;
                                continue;
                            }
                        }
                        inst = inst.next;
                    }
                }
            }
        }
    }
}
