package optimize;

import ir.Block;
import ir.instruction.Inst;
import ir.instruction.PhiInst;
import ir.instruction.TerminalInst;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Loop {
    private Block head;
    private LinkedHashSet<Block> tails = new LinkedHashSet<>();
    private LinkedHashSet<Block> loopBlocks = new LinkedHashSet<>();
    private LinkedHashSet<Loop> childLoops = new LinkedHashSet<>();
    private Block preHeader;

    public Loop(Block head) {
        this.head = head;
    }

    public LinkedHashSet<Block> getLoopBlocks() {
        return loopBlocks;
    }

    public void setPreHeader(Block preHeader) {
        this.preHeader = preHeader;
    }

    public Block getPreHeader() {
        return preHeader;
    }

    public LinkedHashSet<Block> getTails() {
        return tails;
    }

    public void addTail(Block tail) { tails.add(tail); }

    public void addChildLoop(Loop childLoop) {
        childLoops.add(childLoop);
    }

    public LinkedHashSet<Loop> getChildLoops() {
        return childLoops;
    }

    public void appendInstToPreHeader(Inst inst) {
        if (inst instanceof PhiInst) {
            int i;
            if (((PhiInst) inst).getPredecessors().size() != 2)
                throw new RuntimeException();
            for (i = 0; i < ((PhiInst) inst).getPredecessors().size(); ++i) {
                var pred = ((PhiInst) inst).getPredecessors().get(i);
                if (pred != preHeader && !tails.contains(pred))
                    throw new RuntimeException();
                if (pred != preHeader) {
                    var value = ((PhiInst) inst).getValues().get(i);
                    inst.getDstReg().replaceAllUseWith(value);
                }
            }
        }
        else if (!(preHeader.getTailInst() instanceof TerminalInst))
            preHeader.appendInst(inst);
        else preHeader.getTailInst().addPrev(inst);
    }

    public void construct() {
        loopBlocks.add(head);
        loopBlocks.addAll(tails);
        Queue<Block> workList = new LinkedList<>(tails);
        while (!workList.isEmpty()) {
            var block = workList.poll();
            block.getPredecessors().forEach(pred -> {
                if (!loopBlocks.contains(pred)) {
                    workList.add(pred);
                    loopBlocks.add(pred);
                }
            });
        }
    }
}
