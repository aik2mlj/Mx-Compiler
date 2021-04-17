package backend;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.ASMModule;
import riscv.instuctions.*;
import riscv.operands.BaseOffsetAddr;
import riscv.operands.IntImm;
import riscv.operands.RelocationImm;
import riscv.operands.StackAddr;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.*;

public class RegAllocator {
    private static final int K = PhysicalRegister.allocatablePRNames.length;
    private ASMModule module;

    public RegAllocator(ASMModule module) {
        this.module = module;
    }

    private ASMFunction function;
    private HashSet<Move> coalescedMoves = new HashSet<>(),
            constrainedMoves = new HashSet<>(),
            frozenMoves = new HashSet<>(),
            workListMoves = new LinkedHashSet<>(),
            activeMoves = new HashSet<>();
    private HashSet<VirtualRegister> preColored = new HashSet<>();
    private HashSet<VirtualRegister> initial = new HashSet<>();
    private HashSet<VirtualRegister> spillWorkList = new LinkedHashSet<>(),
            freezeWorkList = new LinkedHashSet<>(),
            simplifyWorkList = new LinkedHashSet<>(),
            spilledNodes = new HashSet<>(),
            coloredNodes = new HashSet<>(),
            coalescedNodes = new HashSet<>();
    private Stack<VirtualRegister> selectStack = new Stack<>();
    private HashSet<Edge> adjSet = new HashSet<>();

    private static class Edge {
        VirtualRegister u, v;

        public Edge(VirtualRegister u, VirtualRegister v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return "(" + u.toString() + ", " + v.toString() + ")";
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Edge && ((Edge) obj).u == u && ((Edge) obj).v == v;
        }
    }

    private void addEdge(VirtualRegister u, VirtualRegister v) {
        if (u != v && !adjSet.contains(new Edge(u, v))) {
            adjSet.add(new Edge(u, v));
            adjSet.add(new Edge(v, u));
            if (!preColored.contains(u)) {
                u.getAdjList().add(v);
                u.degree++;
            }
            if (!preColored.contains(v)) {
                v.getAdjList().add(u);
                v.degree++;
            }
        }
    }

    public void run() {
        for (ASMFunction function : module.getFuncMap().values()) {
            this.function = function;
            runForFunc();
        }
    }

    private void runForFunc() {
        boolean done;
        do {
            initialize();
            new LivenessAnalysis(function);
            build();
            makeWorkList();
            do {
                if (!simplifyWorkList.isEmpty()) simplify();
                else if (!workListMoves.isEmpty()) coalesce();
                else if (!freezeWorkList.isEmpty()) freeze();
                else if (!spillWorkList.isEmpty()) selectSpill();
            } while (!(freezeWorkList.isEmpty() && simplifyWorkList.isEmpty()
                    && spillWorkList.isEmpty() && workListMoves.isEmpty()));
            assignColors();

            if (!spilledNodes.isEmpty()) {
                rewriteProgram();
                done = false;
            } else
                done = true;
        } while (!done);

        removeRedundantMove();
        function.getStackFrame().getAllAddr();
//        loadStoreResolve();
        moveStackPointer();
    }

    private void removeRedundantMove() {
        for (ASMBlock block : function.getBlocks()) {
            ASMInst ptr = block.getHeadInst();
            while (ptr != null) {
                ASMInst next = ptr.next;
                if (ptr instanceof Move
                        && ((Move) ptr).getDst().getColor() == ((Move) ptr).getSrc().getColor()) {
                    ((Move) ptr).removeFromBlock();
                }
                ptr = next;
            }
        }
    }

    private void initialize() {
        preColored.clear();
        simplifyWorkList.clear();
        freezeWorkList.clear();
        spillWorkList.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        coloredNodes.clear();
        selectStack.clear();

        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        activeMoves.clear();
        workListMoves.clear();

        adjSet.clear();

        preColored.addAll(PhysicalRegister.vrs.values());
        initial.addAll(function.getSymbolTable().getVrMap().values());
        initial.removeAll(preColored);
        initial.forEach(VirtualRegister::clearColorData);
        int inf = 1147483640;
        preColored.forEach(vr -> vr.degree = inf);
    }

    private void build() {
        for (ASMBlock block : function.getBlocks()) {
            var live = block.getLiveOut();
            for (var inst = block.getTailInst(); inst != null; inst = inst.prev) {
                if (inst instanceof Move) {
                    live.removeAll(inst.getUses());
                    for (VirtualRegister vr : inst.getUses()) {
                        vr.getMoveList().add((Move) inst);
                    }
                    for (VirtualRegister vr : inst.getDefs()) {
                        vr.getMoveList().add((Move) inst);
                    }
                    workListMoves.add((Move) inst);
                }
                live.add(PhysicalRegister.zeroVR);
                live.addAll(inst.getDefs());
                inst.getDefs().forEach(def -> {
                    live.forEach(l -> {
                        addEdge(l, def);
                    });
                });
                live.removeAll(inst.getDefs());
                live.addAll(inst.getUses());
            }
        }
    }

    private void makeWorkList() {
        for (VirtualRegister vr : initial) {
            if (vr.degree >= K)
                spillWorkList.add(vr);
            else if (moveRelated(vr))
                freezeWorkList.add(vr);
            else
                simplifyWorkList.add(vr);
        }
    }

    private HashSet<VirtualRegister> adjacent(VirtualRegister vr) {
        HashSet<VirtualRegister> ret = new HashSet<>(vr.getAdjList());
        ret.removeAll(selectStack);
        ret.removeAll(coalescedNodes);
        return ret;
    }

    private boolean moveRelated(VirtualRegister vr) {
        return !nodeMoves(vr).isEmpty();
    }

    private HashSet<Move> nodeMoves(VirtualRegister vr) {
        HashSet<Move> ret = new HashSet<>(activeMoves);
        ret.addAll(workListMoves);
        ret.retainAll(vr.getMoveList());
        return ret;
    }

    private void simplify() {
        var vr = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(vr);
        selectStack.push(vr);
        vr.getAdjList().forEach(this::decrementDegree);
    }

    private void decrementDegree(VirtualRegister vr) {
        int degree = vr.degree--;
        if (degree == K) {
            HashSet<VirtualRegister> nodes = new HashSet<>(adjacent(vr));
            nodes.add(vr);
            enableMoves(nodes);
            spillWorkList.remove(vr);
            if (moveRelated(vr))
                freezeWorkList.add(vr);
            else
                simplifyWorkList.add(vr);
        }
    }

    private void enableMoves(HashSet<VirtualRegister> nodes) {
        nodes.forEach(n -> nodeMoves(n).forEach(m -> {
            if (activeMoves.contains(m)) {
                activeMoves.remove(m);
                workListMoves.add(m);
            }
        }));
    }

    private void enableMoves(VirtualRegister node) {
        nodeMoves(node).forEach(m -> {
            if (activeMoves.contains(m)) {
                activeMoves.remove(m);
                workListMoves.add(m);
            }
        });
    }

    private void addWorkList(VirtualRegister u) {
        if (!preColored.contains(u) && !moveRelated(u) && u.degree < K) {
            freezeWorkList.remove(u);
            simplifyWorkList.add(u);
        }
    }

    private boolean OK(VirtualRegister t, VirtualRegister r) {
        return t.degree < K || preColored.contains(t) || adjSet.contains(new Edge(t, r));
    }

    private boolean conservative(HashSet<VirtualRegister> nodes) {
        int k = 0;
        for (VirtualRegister n : nodes) {
            if (n.degree >= K)
                ++k;
        }
        return k < K;
    }

    private void coalesce() {
        Move m = workListMoves.iterator().next();
        workListMoves.remove(m);
        var x = getAlias(m.getDst());
        var y = getAlias(m.getSrc());
        VirtualRegister u, v;
        if (preColored.contains(y)) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }
        workListMoves.remove(m);
        HashSet<VirtualRegister> adjacentUV = new HashSet<>(adjacent(u));
        adjacentUV.addAll(adjacent(v));
        if (u == v) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if (preColored.contains(v) || adjSet.contains(new Edge(u, v))) {
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if ((preColored.contains(u) && everyAdjacentIsOK(u, v)) || (!preColored.contains(u) && conservative(adjacentUV))) {
            coalescedMoves.add(m);
            combine(u, v);
            addWorkList(u);
        } else
            activeMoves.add(m);
    }

    private void combine(VirtualRegister u, VirtualRegister v) {
        if (freezeWorkList.contains(v))
            freezeWorkList.remove(v);
        else
            spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.setAlias(u);
        u.getMoveList().addAll(v.getMoveList());
        enableMoves(v);

        for (VirtualRegister t : adjacent(v)) {
            addEdge(t, u);
            decrementDegree(t);
        }
        if (u.degree >= K && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }

    private boolean everyAdjacentIsOK(VirtualRegister u, VirtualRegister v) {
        for (VirtualRegister t : adjacent(v)) {
            if (!OK(t, u))
                return false;
        }
        return true;
    }

    private VirtualRegister getAlias(VirtualRegister n) {
        if (coalescedNodes.contains(n))
            return getAlias(n.getAlias());
        else return n;
    }

    private void freeze() {
        var u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }

    private void freezeMoves(VirtualRegister u) {
        nodeMoves(u).forEach(m -> {
            VirtualRegister x = m.getDst(), y = m.getSrc(), v;
            if (getAlias(u) == getAlias(y))
                v = getAlias(x);
            else v = getAlias(y);
            activeMoves.remove(m);
            frozenMoves.add(m);
            if (v.degree < K && nodeMoves(v).isEmpty()) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        });
    }

    private void selectSpill() {
        // TODO: loop analysis
        var m = spillWorkList.iterator().next();
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }

    private void assignColors() {
        while (!selectStack.isEmpty()) {
            var n = selectStack.pop();
            HashSet<PhysicalRegister> okColors = new LinkedHashSet<>(PhysicalRegister.allocatablePRs);
            n.getAdjList().forEach(w -> {
                var aliasW = getAlias(w);
                if (coloredNodes.contains(aliasW) || preColored.contains(aliasW)) {
                    okColors.remove(getAlias(w).getColor());
                }
            });
            if (okColors.isEmpty())
                spilledNodes.add(n);
            else {
                coloredNodes.add(n);
                var c = selectColor(okColors);
                n.setColor(c);
            }
        }
        coalescedNodes.forEach(n -> n.setColor(getAlias(n).getColor()));
    }

    private PhysicalRegister selectColor(HashSet<PhysicalRegister> okColors) {
        for (PhysicalRegister pr : okColors) {
            if (PhysicalRegister.callerSavePRs.containsKey(pr.getName()))
                return pr;
        }
        return okColors.iterator().next();
    }

    private void rewriteProgram() {
        for (VirtualRegister vr : spilledNodes) {
            StackAddr stackAddr = new StackAddr(vr.getName());
            function.getStackFrame().getSpillAddrMap().put(vr, stackAddr);
            HashSet<ASMInst> defs = new HashSet<>(vr.getDefs());
            HashSet<ASMInst> uses = new HashSet<>(vr.getUses());

            int cnt = 0;
            for (ASMInst defInst : defs) {
                VirtualRegister spillVR = new VirtualRegister(vr.getName() + "_spill" + cnt++);
                function.getSymbolTable().addVR(spillVR);

                defInst.replaceDef(vr, spillVR);
                defInst.addNext(new St(defInst.getParentBlock(), St.ByteSize.sw, spillVR, stackAddr));
            }
            for (ASMInst defInst : uses) {
                VirtualRegister spillVR = new VirtualRegister(vr.getName() + "_spill" + cnt++);
                function.getSymbolTable().addVR(spillVR);

                defInst.replaceUse(vr, spillVR);
                defInst.addPrev(new Ld(defInst.getParentBlock(), Ld.ByteSize.lw, spillVR, stackAddr));
            }
            function.getSymbolTable().getVrMap().remove(vr.getName());
        }
    }

//     private void loadStoreResolve() {
//         for (ASMBlock block : function.getBlocks()) {
//             for (ASMInst inst = block.getHeadInst(); inst != null; inst = inst.next) {
//                 BaseOffsetAddr addr;
//                 if (inst instanceof Ld && ((Ld) inst).getAddr() instanceof BaseOffsetAddr)
//                     addr = (BaseOffsetAddr) ((Ld) inst).getAddr();
//                 else if (inst instanceof St && ((St) inst).getAddr() instanceof BaseOffsetAddr)
//                     addr = (BaseOffsetAddr) ((St) inst).getAddr();
//                 else
//                     continue;
//                 if (addr.getBase().getColor() != null) {
// //                    System.err.println("resolve not: " + inst.emit());
//                     continue;
//                 }
//                 if (addr.getOffset() instanceof RelocationImm) {
//                     continue;
//                 }
//                 if (addr.getBase().isAollocated()) {
//                     System.err.println("here");
//                     StackAddr stackAddr = function.getStackFrame().getSpillAddrMap().get(addr.getBase());
//                     StackAddr newAddr = new StackAddr(addr.getBase().getName() + "_offset");
//                     assert addr.getOffset() instanceof IntImm;
//                     newAddr.setOffset(((IntImm) addr.getOffset()).getValue() + stackAddr.getOffset());

//                     if (inst instanceof Ld)
//                         ((Ld) inst).setAddr(newAddr);
//                     else ((St) inst).setAddr(newAddr);
//                 }
//             }
//         }
//     }

    private void moveStackPointer() {
        int frameSize = function.getStackFrame().getSize();
        System.err.println(frameSize);
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
