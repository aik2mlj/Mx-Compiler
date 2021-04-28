package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.PhiInst;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class LoopTreeConstructor {
    private Function function;
    private boolean addPreHeader;
    public HashMap<Block, Loop> loopMap = new HashMap<>();
    public HashSet<Loop> globalLoops = new HashSet<>();
    private HashSet<Block> visited = new HashSet<>();
    private Stack<Loop> loopStack = new Stack<>();

    public LoopTreeConstructor(Function function, boolean addPreHeader) {
        this.function = function;
        this.addPreHeader = addPreHeader;
    }

    private void collectLoop(Block head, Block tail) {
        if (!loopMap.containsKey(head)) {
            loopMap.put(head, new Loop(head));
        }
        loopMap.get(head).addTail(tail);
    }

    public void runFunc() {
        loopMap.clear();
        globalLoops.clear();
        visited.clear();
        loopStack.clear();
        // find all backward edges
        for (Block block : function.getBlocks()) {
            for (Block pred : block.getPredecessors()) {
                if (block.dominates(pred)) {
                    collectLoop(block, pred);
                    break;
                }
            }
        }
        // add pre header
        if (addPreHeader) {
            loopMap.forEach((head, loop) -> {
                Block preHeader = new Block(function, "preheader");
                function.appendBlock(preHeader);
                loop.setPreHeader(preHeader);
                // if a pred is one of the loop tails, do not change cfg
                // else: replace its successor
                var preds = new HashSet<>(head.getPredecessors());
                for (Block pred : preds) {
                    if (!loop.getTails().contains(pred))
                        pred.replaceSuc(head, preHeader);
                }
                var phis = new ArrayList<>(head.getPhiInsts());
                phis.forEach(phiInst -> { // move phis to preHeader
                    var preHeaderPhiPreds = new ArrayList<Block>();
                    var headPhiPreds = new ArrayList<Block>();
                    var preHeaderPhiValues = new ArrayList<Operand>();
                    var headPhiValues = new ArrayList<Operand>();
                    for (int i = 0; i < phiInst.getPredecessors().size(); ++i) {
                        var pred = phiInst.getPredecessors().get(i);
                        if (loop.getTails().contains(pred)) {
                            headPhiPreds.add(pred);
                            headPhiValues.add(phiInst.getValues().get(i));
                        } else {
                            preHeaderPhiPreds.add(pred);
                            preHeaderPhiValues.add(phiInst.getValues().get(i));
                        }
                    }
                    phiInst.removeFromBlock();
                    if (!headPhiPreds.isEmpty()) {
                        Register preHeaderPhi = new Register(phiInst.getDstReg().getType(), "preheader." + phiInst.getDstReg().getName(), function);
                        preHeader.appendInst(new PhiInst(preHeader, preHeaderPhiPreds, preHeaderPhiValues, preHeaderPhi));
                        headPhiPreds.add(preHeader);
                        headPhiValues.add(preHeaderPhi);
                        head.pushFrontInst(new PhiInst(head, headPhiPreds, headPhiValues, phiInst.getDstReg()));
                    } else {
                        preHeader.appendInst(phiInst);
                    }
                });
                preHeader.cleanPhis();
                preHeader.appendBrInstTo_U(head);
                if (function.getEntryBlock() == head) {
                    // is entry block
                    function.setEntryBlock(preHeader);
                }
            });
        }
        // get loops
        loopMap.forEach((head, loop) -> {
            loop.construct();
        });
        // get Loop Tree
        constructTree(function.getEntryBlock());
    }

    private void constructTree(Block block) {
        visited.add(block);
        // clear out loopStack
        if (!loopStack.isEmpty()) {
            while (!loopStack.isEmpty() && !loopStack.peek().getLoopBlocks().contains(block))
                loopStack.pop();
        }
        if (loopMap.containsKey(block)) {
            // this is the head of a loop
            var loop = loopMap.get(block);
            if (loopStack.isEmpty()) // global loop: is not child of any loop
                globalLoops.add(loop);
            else {
                loopStack.peek().addChildLoop(loop); // this is a child of a bigger loop
//                loopStack.peek().getLoopBlocks().removeAll(loop.getLoopBlocks()); // remove childLoop blocks
            }
            loopStack.push(loop);
        }
        block.setLoopDepth(loopStack.size());
        block.getSuccessors().forEach(suc -> {
            if (!visited.contains(suc))
                constructTree(suc);
        });
    }
}
