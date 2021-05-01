package backend;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.ASMModule;
import riscv.instuctions.*;
import riscv.operands.IntImm;
import riscv.operands.StackAddr;
import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.*;

public class RegisterAllocator {
    private ASMModule module;

    private static class Edge {
        public VirtualRegister u, v;

        public Edge(VirtualRegister u, VirtualRegister v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge))
                return false;
            return toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            return "(" + (PhysicalRegister.vrs.containsValue(u)? "$" : "") + u.getName() + ", " +
                    (PhysicalRegister.vrs.containsValue(v)? "$" : "") + v.getName() + ")";
        }
    }

    static private final int K = PhysicalRegister.allocatablePRs.size();

    private ASMFunction function;


    public RegisterAllocator(ASMModule module) {
        this.module = module;
    }

    private Set<VirtualRegister> preColored;
    private Set<VirtualRegister> initial;
    private Set<VirtualRegister> simplifyWorkList;
    private Set<VirtualRegister> freezeWorkList;
    private Set<VirtualRegister> spillWorkList;
    private Set<VirtualRegister> spilledNodes;
    private Set<VirtualRegister> coalescedNodes;
    private Set<VirtualRegister> coloredNodes;
    private Stack<VirtualRegister> selectStack;

    private Set<Move> coalescedMoves;
    private Set<Move> constrainedMoves;
    private Set<Move> frozenMoves;
    private Set<Move> workListMoves;
    private Set<Move> activeMoves;

    private Set<Edge> adjSet;

    public void run() {
        for (ASMFunction function : module.getFuncMap().values())
            runFunc(function);
    }

    private void runFunc(ASMFunction function) {
        this.function = function;
        while (true) {
            initialize();
            // computeSpillCost();
            new LivenessAnalysis(function).run();
            build();
            makeWorkList();

            while (!simplifyWorkList.isEmpty()
                    || !workListMoves.isEmpty()
                    || !freezeWorkList.isEmpty()
                    || !spillWorkList.isEmpty()) {
                if (!simplifyWorkList.isEmpty())
                    simplify();
                else if (!workListMoves.isEmpty())
                    coalesce();
                else if (!freezeWorkList.isEmpty())
                    freeze();
                else
                    selectSpill();
            }
            assignColors();

            if (!spilledNodes.isEmpty())
                rewriteProgram();
            else
                break;
        }
        checkColor();
        removeRedundantMoveInst();
        function.getStackFrame().getAllAddr();
        moveStackPointer();
    }

    private void checkColor() {
        ArrayList<ASMBlock> dfsOrder = function.getDFSBlocks();
        for (ASMBlock block : dfsOrder) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                for (VirtualRegister vr : inst.getDefs())
                    if (vr.getColor() == null) System.err.println(inst + " #" + vr);
                for (VirtualRegister vr : inst.getUses())
                    if (vr.getColor() == null) System.err.println(inst + " #" + vr);
            }
        }
    }

    private void initialize() {
        preColored = new HashSet<>();
        initial = new HashSet<>();
        simplifyWorkList = new LinkedHashSet<>();
        freezeWorkList = new LinkedHashSet<>();
        spillWorkList = new LinkedHashSet<>();
        spilledNodes = new HashSet<>();
        coalescedNodes = new HashSet<>();
        coloredNodes = new HashSet<>();
        selectStack = new Stack<>();

        coalescedMoves = new HashSet<>();
        constrainedMoves = new HashSet<>();
        frozenMoves = new HashSet<>();
        workListMoves = new LinkedHashSet<>();
        activeMoves = new HashSet<>();

        adjSet = new HashSet<>();


        initial.addAll(function.getSymbolTable().getVrMap().values());
        preColored.addAll(PhysicalRegister.vrs.values());
        initial.removeAll(preColored);


        for (VirtualRegister vr : initial)
            vr.clearColorData();
        int inf = 1000000000;
        for (VirtualRegister vr : preColored)
            vr.degree = inf;
    }

    private void build() {
        var dfsOrder = new ArrayList<>(function.getBlocks());
        for (ASMBlock block : dfsOrder) {
            Set<VirtualRegister> live = block.getLiveOut();
            ASMInst inst = block.getTailInst();
            while (inst != null) {
                if (inst instanceof Move) {
                    live.removeAll(inst.getUses());
                    for (VirtualRegister n : inst.getDefs())
                        n.getMoveList().add(((Move) inst));
                    for (VirtualRegister n : inst.getUses())
                        n.getMoveList().add(((Move) inst));
                    workListMoves.add(((Move) inst));
                }

                live.add(PhysicalRegister.zeroVR);
                live.addAll(inst.getDefs());
                for (VirtualRegister d : inst.getDefs()) {
                    for (VirtualRegister l : live)
                        addEdge(l, d);
                }
                live.removeAll(inst.getDefs());
                live.addAll(inst.getUses());

                inst = inst.prev;
            }
        }
    }

    private void addEdge(VirtualRegister u, VirtualRegister v) {
        if (!adjSet.contains(new Edge(u, v)) && u != v) {
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

    private void makeWorkList() {
        for (VirtualRegister n : initial) {
            if (n.degree >= K)
                spillWorkList.add(n);
            else if (moveRelated(n))
                freezeWorkList.add(n);
            else
                simplifyWorkList.add(n);
        }
        // We don't have to clear "initial".
    }

    private Set<VirtualRegister> adjacent(VirtualRegister n) {
        Set<VirtualRegister> res = new LinkedHashSet<>(n.getAdjList());
        selectStack.forEach(res::remove);
        res.removeAll(coalescedNodes);
        return res;
    }

    private Set<Move> nodeMoves(VirtualRegister n) {
        LinkedHashSet<Move> res = new LinkedHashSet<>(activeMoves);
        res.addAll(workListMoves);
        res.retainAll(n.getMoveList());
        return res;
    }

    private boolean moveRelated(VirtualRegister n) {
        return !nodeMoves(n).isEmpty();
    }

    private void simplify() {
        assert !simplifyWorkList.isEmpty();
        VirtualRegister n = simplifyWorkList.iterator().next();
        simplifyWorkList.remove(n);
        selectStack.push(n);
        for (VirtualRegister m : adjacent(n))
            decrementDegree(m);
    }

    private void decrementDegree(VirtualRegister m) {
        int d = m.degree--;
        if (d == K) {
            Set<VirtualRegister> union = new LinkedHashSet<>(adjacent(m));
            union.add(m);
            enableMoves(union);
            spillWorkList.remove(m);
            if (moveRelated(m))
                freezeWorkList.add(m);
            else
                simplifyWorkList.add(m);
        }
    }

    private void enableMoves(Set<VirtualRegister> nodes) {
        for (VirtualRegister n : nodes) {
            for (Move m : n.getMoveList())
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    workListMoves.add(m);
                }
//            for (Move m : nodeMoves(n)) {
//                if (activeMoves.contains(m)) {
//                    activeMoves.remove(m);
//                    workListMoves.add(m);
//                }
//            }
        }
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

    private boolean conservative(Set<VirtualRegister> nodes) {
        int k = 0;
        for (VirtualRegister n : nodes) {
            if (n.degree >= K)
                k++;
        }
        return k < K;
    }

    private void coalesce() {
        assert !workListMoves.isEmpty();
        Move m = workListMoves.iterator().next();
        workListMoves.remove(m);
        VirtualRegister x = getAlias(m.getDst()), y = getAlias(m.getSrc());
        VirtualRegister u, v;
        if (preColored.contains(y)) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }

        Set<VirtualRegister> adjacentUV = new LinkedHashSet<>(adjacent(u));
        adjacentUV.addAll(adjacent(v));
        if (u == v) {
            coalescedMoves.add(m);
            addWorkList(u);
        } else if (preColored.contains(v) || adjSet.contains(new Edge(u, v))) {
            constrainedMoves.add(m);
            addWorkList(u);
            addWorkList(v);
        } else if ((preColored.contains(u) && everyAdjacentNodeIsOK(v, u))
                || (!preColored.contains(u) && conservative(adjacentUV))) {
            coalescedMoves.add(m);
            combine(u, v);
            addWorkList(u);
        } else
            activeMoves.add(m);
    }

    private boolean everyAdjacentNodeIsOK(VirtualRegister v, VirtualRegister u) {
        for (VirtualRegister t : adjacent(v)) {
            if (!OK(t, u))
                return false;
        }
        return true;
    }

    private void combine(VirtualRegister u, VirtualRegister v) {
        if (freezeWorkList.contains(v))
            freezeWorkList.remove(v);
        else
            spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.setAlias(u);
        u.getMoveList().addAll(v.getMoveList());

        Set<VirtualRegister> nodes = new LinkedHashSet<>();
        nodes.add(v);
        enableMoves(nodes);

        for (VirtualRegister t : adjacent(v)) {
            addEdge(t, u);
            decrementDegree(t);
        }
        if (u.degree >= K && freezeWorkList.contains(u)) {
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }

    private VirtualRegister getAlias(VirtualRegister n) {
        if (coalescedNodes.contains(n)) {
            VirtualRegister alias = getAlias(n.getAlias());
            n.setAlias(alias);
            return alias;
        } else
            return n;
    }

    private void freeze() {
        var u = freezeWorkList.iterator().next();
        freezeWorkList.remove(u);
        simplifyWorkList.add(u);
        freezeMoves(u);
    }

    private void freezeMoves(VirtualRegister u) {
        for (Move m : nodeMoves(u)) {
            VirtualRegister x = m.getDst(), y = m.getSrc(), v;
            if (getAlias(y) == getAlias(u))
                v = getAlias(x);
            else
                v = getAlias(y);
            activeMoves.remove(m);
            frozenMoves.add(m);

            if (freezeWorkList.contains(v) && nodeMoves(v).isEmpty()) {
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        }
    }

    private void selectSpill() {
        VirtualRegister m = selectVRToBeSpilled();
        spillWorkList.remove(m);
        simplifyWorkList.add(m);
        freezeMoves(m);
    }

    private VirtualRegister selectVRToBeSpilled() {
        return spillWorkList.iterator().next();
    }

    private void assignColors() {
        while (!selectStack.isEmpty()) {
            VirtualRegister n = selectStack.pop();
            Set<PhysicalRegister> okColors = new LinkedHashSet<>(PhysicalRegister.allocatablePRs);
            for (VirtualRegister w : n.getAdjList()) {
                var w_alias = getAlias(w);
                if (coloredNodes.contains(w_alias) || preColored.contains(w_alias))
                    okColors.remove(getAlias(w).getColor());
            }

            if (okColors.isEmpty())
                spilledNodes.add(n);
            else {
                coloredNodes.add(n);
                PhysicalRegister c = selectColor(okColors);
                n.setColor(c);
            }
        }
        coalescedNodes.forEach(n -> n.setColor(getAlias(n).getColor()));
    }

    private PhysicalRegister selectColor(Set<PhysicalRegister> okColors) {
        assert !okColors.isEmpty();
        for (PhysicalRegister pr : okColors) {
            if (PhysicalRegister.callerSavePRs.containsKey(pr.getName()))
                return pr;
        }
        return okColors.iterator().next();
    }

    private void rewriteProgram() {
        for (VirtualRegister vr : spilledNodes) {
            if (PhysicalRegister.vrs.containsValue(vr)) throw new RuntimeException();
//            System.err.println("spill: " + vr + ", " + function.getName());
            StackAddr stackLocation = new StackAddr(vr.getName());
            function.getStackFrame().getSpillAddrMap().put(vr, stackLocation);
            Set<ASMInst> defs = new LinkedHashSet<>(vr.getDefs());
            Set<ASMInst> uses = new LinkedHashSet<>(vr.getUses());

            int cnt = 0;
            for (ASMInst inst : defs) {
                VirtualRegister spilledVR = new VirtualRegister(vr.getName() + ".spill." + cnt);
                function.getSymbolTable().addVRRename(spilledVR);
                cnt++;

                ASMBlock block = inst.getParentBlock();
                inst.replaceDef(vr, spilledVR);
                inst.addNext(new St(block, St.ByteSize.sw, spilledVR, stackLocation));
            }
            for (ASMInst inst : uses) {
                VirtualRegister spilledVR = new VirtualRegister(vr.getName() + ".spill." + cnt);
                function.getSymbolTable().addVRRename(spilledVR);
                cnt++;

                ASMBlock block = inst.getParentBlock();
                inst.replaceUse(vr, spilledVR);
                inst.addPrev(new Ld(block, Ld.ByteSize.lw, spilledVR, stackLocation));
            }
            if (!vr.getDefs().isEmpty() || !vr.getUses().isEmpty())
                throw new RuntimeException();
            function.getSymbolTable().removeVR(vr);
        }
    }

    private void removeRedundantMoveInst() {
        ArrayList<ASMBlock> dfsOrder = new ArrayList<>(function.getBlocks());
        for (ASMBlock block : dfsOrder) {
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

    private void moveStackPointer() {
        int frameSize = function.getStackFrame().getSize();
        if (frameSize == 0)
            return;

        VirtualRegister sp = PhysicalRegister.vrs.get("sp");
        function.getEntryBlock().pushFrontInst(new IBinary(function.getEntryBlock(),
                IBinary.Operator.addi, sp, sp, new IntImm(-frameSize * 4)));

        for (ASMBlock block : function.getBlocks()) {
            if (block.getTailInst() instanceof Ret) {
                block.getTailInst().addPrev(new IBinary(block,
                        IBinary.Operator.addi, sp, sp, new IntImm(frameSize * 4)));
                break;
            }
        }
    }
}
