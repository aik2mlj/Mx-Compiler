package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.CallInst;

import java.util.*;

public class Inliner extends IRPass {
    private HashMap<Function, HashSet<Function>> recursiveCalleeMap = new HashMap<>();
    private HashMap<Function, Integer> instCnt = new HashMap<>();
    private static final int instCntLimit = 128;

    public Inliner(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        module.getFuncMap().values().forEach(function -> {
            recursiveCalleeMap.put(function, new HashSet<>());
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
        function.getBlocks().forEach(block -> {
            int cnt = 0;
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                cnt++;
                if (inst instanceof CallInst) {
                    recursiveCalleeMap.get(function).add(((CallInst) inst).getFunction());
                }
            }
            instCnt.put(function, cnt);
        });
    }

    private void resolveCalls() {
        Queue<Function> workList = new LinkedList<>();
        for (Function function : module.getFuncMap().values()) {
            workList.clear();
            var recursiveCallee = recursiveCalleeMap.get(function);
            workList.addAll(recursiveCallee);
            while (!workList.isEmpty()) {
                var callee = workList.poll();
                recursiveCalleeMap.get(callee).forEach(callcallee -> {
                    if (!recursiveCallee.contains(callcallee)) {
                        recursiveCallee.add(callcallee);
                        workList.add(callcallee);
                    }
                });
            }
        }
    }

    private void nonRecursiveInline() {
        boolean change_;
        do {
            change_ = false;
            for (Function function : module.getFuncMap().values()) {
                for (Block block : function.getBlocks()) {
                    for (var inst = block.getHeadInst(); inst != null;) {
                        if (inst instanceof CallInst) {
                            var callee = ((CallInst) inst).getFunction();
                            if (module.getFuncMap().containsValue(callee) &&
                                    instCnt.get(callee) < instCntLimit && callee != function && !recursiveCalleeMap.get(callee).contains(callee)) {

                            }
                        }
                        inst = inst.next;
                    }
                }
            }
        } while (changed);
    }

    private void recursiveInline() {
    }
}
