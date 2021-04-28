package backend;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.ASMModule;
import riscv.instuctions.ASMInst;
import riscv.operands.register.VirtualRegister;

import java.util.ArrayList;
import java.util.HashSet;

//public class LivenessAnalysis {
//    private ASMFunction function;
//    private HashSet<ASMBlock> visitedBlocks;
//
//    private boolean equal(HashSet<VirtualRegister> a, HashSet<VirtualRegister> b) {
//        if (a.size() != b.size()) return false;
//        else return a.containsAll(b);
//    }
//
//    public LivenessAnalysis(ASMFunction function) {
//        this.function = function;
//        visitedBlocks = new HashSet<>();
//    }
//
//    public void run() {
//        // get uses and defs for each block
//        function.getBlocks().forEach(this::getBlockUsesAndDefs);
//        // update starting from the exit block: backward DFS
////        updateLiveInOut(function.getBlocks().getLast());
//
//        HashSet<VirtualRegister> _liveOut;
//        HashSet<VirtualRegister> _liveIn;
//        boolean done;
//        do {
//            done = true;
//            for (ASMBlock n : function.getBlocks()) {
//                _liveOut = new HashSet<>(n.getLiveOut());
//                _liveIn = new HashSet<>(n.getLiveIn());
//                n.getSuccessors().forEach(suc -> n.getLiveOut().addAll(suc.getLiveIn()));
//                n.getLiveIn().addAll(n.getLiveOut());
//                n.getLiveIn().removeAll(n.getDefs());
//                n.getLiveIn().addAll(n.getUses());
//                if (!equal(_liveIn, n.getLiveIn()) || !equal(_liveOut, n.getLiveOut()))
//                    done = false;
//            }
//        } while (!done);
//    }
//
//    private void getBlockUsesAndDefs(ASMBlock block) {
//        HashSet<VirtualRegister> uses = new HashSet<>();
//        HashSet<VirtualRegister> defs = new HashSet<>();
//        for (ASMInst inst = block.getHeadInst(); inst != null; inst = inst.next) {
//            var instUse = inst.getUses();
//            instUse.removeAll(defs);
//            uses.addAll(instUse);
//            defs.addAll(inst.getDefs());
//        }
//        block.setUses(uses);
//        block.setDefs(defs);
//        block.getLiveIn().clear();
//        block.getLiveOut().clear();
//    }
//
//    private void updateLiveInOut(ASMBlock block) {
//        HashSet<VirtualRegister> _liveOut = new HashSet<>();
//
//        visitedBlocks.add(block);
//        // liveOut is counted and is determined since this is a backward DFS.
//        block.getSuccessors().forEach(succ -> _liveOut.addAll(succ.getLiveIn()));
//        HashSet<VirtualRegister> _liveIn = new HashSet<>(_liveOut);
//        _liveIn.removeAll(block.getDefs());
//        _liveIn.addAll(block.getUses());
//        block.getLiveOut().addAll(_liveOut);
//        _liveIn.removeAll(block.getLiveIn());
//        // liveIn may influence block.predecessors. So DFS them afterward.
//        if (!_liveIn.isEmpty()) {
//            block.getLiveIn().addAll(_liveIn);
//            visitedBlocks.removeAll(block.getPredecessors());
//        }
//        block.getPredecessors().forEach(pred -> {
//            if (!visitedBlocks.contains(pred))
//                updateLiveInOut(pred);
//        });
//    }
//}


public class LivenessAnalysis {
    private ASMFunction function;

    public LivenessAnalysis(ASMFunction function) {
        this.function = function;
    }

    public void run() {
        computeLiveOutSet(function);
    }

    private void computeLiveOutSet(ASMFunction function) {
        ArrayList<ASMBlock> dfsOrder = function.getDFSBlocks();
        for (ASMBlock block : dfsOrder)
            getBlockUsesAndDefs(block);

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = dfsOrder.size() - 1; i >= 0; i--) {
                ASMBlock block = dfsOrder.get(i);
                HashSet<VirtualRegister> liveOut = computeLiveOutSet(block);
                if (!block.getLiveOut().equals(liveOut)) {
                    block.setLiveOut(liveOut);
                    changed = true;
                }
            }
        }
    }

    private void getBlockUsesAndDefs(ASMBlock block) {
        HashSet<VirtualRegister> uses = new HashSet<>();
        HashSet<VirtualRegister> defs = new HashSet<>();
        for (ASMInst inst = block.getHeadInst(); inst != null; inst = inst.next) {
            var instUse = inst.getUses();
            instUse.removeAll(defs);
            uses.addAll(instUse);
            defs.addAll(inst.getDefs());
        }
        block.setUses(uses);
        block.setDefs(defs);
        block.getLiveIn().clear();
        block.getLiveOut().clear();
    }

    private HashSet<VirtualRegister> computeLiveOutSet(ASMBlock block) {
        HashSet<VirtualRegister> liveOut = new HashSet<>();
        for (ASMBlock successor : block.getSuccessors()) {
            HashSet<VirtualRegister> intersection = new HashSet<>(successor.getLiveOut());
            intersection.removeAll(successor.getDefs());

            HashSet<VirtualRegister> union = new HashSet<>(successor.getUses());
            union.addAll(intersection);

            liveOut.addAll(union);
        }
        return liveOut;
    }
}
