package ir;

import ir.instruction.BrInst;
import ir.instruction.PhiInst;
import ir.operand.Operand;

import java.util.*;

public class CFGSimplifier extends IRPass {
    private boolean superClean;

    public CFGSimplifier(Module module, boolean superClean) {
        super(module);
        this.superClean = superClean;
    }

    @Override
    public boolean run() {
        changed = false;
        module.getFuncMap().values().forEach(this::runFunc);
        return changed;
    }

    @Override
    protected void runFunc(Function function) {
        SimpleClean(function);
        if (superClean)
            while (superCleanOnce(function));
    }

    private void SimpleClean(Function function) {
        Queue<Block> workList = new LinkedList<>(function.getBlocks());
        while (!workList.isEmpty()) {
            var block = workList.poll();
            if (block.getPredecessors().isEmpty() && function.getEntryBlock() != block) {
                // not entry && no preds: just delete it
                new IRPrinter("IRcout.ll", module);
                function.removeBlock(block);
                workList.addAll(block.getSuccessors());
            }
        }
    }

    private boolean superCleanOnce(Function function) {
        boolean retChange = false;
        for (Block block : function.getPostDSFBlocks()) {
            if (block.getTailInst() instanceof BrInst &&
                    ((BrInst) block.getTailInst()).getTrueBlock() == ((BrInst) block.getTailInst()).getFalseBlock()) {
//                System.err.println("case 1: " + block.getName() + ", " + function.getName());
                // if i ends in a conditional branch && both targets are identical
                block.replaceBrInst(new BrInst(block, null, ((BrInst) block.getTailInst()).getTrueBlock(), null));
                retChange = true;
            }
            if (block.getSuccessors().size() == 1) { // ends in a jump
                var suc = block.getSuccessors().iterator().next();
                if (suc.getPredecessors().size() == 1) { // only has one pred && this is the only suc of that pred: merge
//                    System.err.println("case 3: " + block.getName() + ", " + function.getName());
                    suc.mergeInto(block);
                    retChange = true;
                } else if (block.getHeadInst().next == null && function.getEntryBlock() != block) { // empty block
                    var preds = new LinkedHashSet<>(block.getPredecessors());
                    boolean cannotMerge = false;
                    for (Block pred : preds) {
                        if (suc.getPredecessors().contains(pred)) {
                            cannotMerge = true;
                            break;
                        }
                    }
                    if (cannotMerge) continue;
//                    new IRPrinter("IRcout.ll", module);
//                    System.err.println("case 2: " + block.getName() + ", " + function.getName());
                    preds.forEach(pred -> pred.replaceSuc(block, suc));
                    for (PhiInst phiInst : suc.getPhiInsts()) {
                        Operand value = null;
                        for (int i = 0; i < phiInst.getPredecessors().size(); ++i) {
                            if (phiInst.getPredecessors().get(i) == block) {
                                value = phiInst.getValues().get(i);
                                break;
                            }
                        }
//                        System.err.println("phi: " + phiInst + " # " + value);
                        if (value != null) {
                            for (Block pred : preds) {
                                phiInst.getPredecessors().add(pred);
                                phiInst.getValues().add(value);
                                value.addUse(phiInst); // HOLY $H!T
                            }
                        } else throw new RuntimeException();
                    }
                    function.removeBlock(block);
                    suc.cleanPhis();
                    retChange = true;
                } else if (suc.getHeadInst() == suc.getTailInst() && suc.getTailInst() instanceof BrInst && ((BrInst) suc.getTailInst()).getCondition() != null) {
                    // suc is empty and ends in a conditional branch: hoist the branch to block
                    // add block to phis in suc's sucs
//                    System.err.println("case 4: " + block.getName() + ", " + function.getName());
                    suc.getSuccessors().forEach(susuc -> {
                        susuc.getPhiInsts().forEach(phiInst -> {
                            for (int i = 0; i < phiInst.getPredecessors().size(); ++i) {
                                if (phiInst.getPredecessors().get(i) == suc) {
                                    phiInst.getPredecessors().add(block);
                                    phiInst.getValues().add(phiInst.getValues().get(i));
                                }
                            }
                        });
                    });
                    BrInst brInst = (BrInst) suc.getTailInst();
                    block.replaceBrInst(new BrInst(block, brInst.getCondition(), brInst.getTrueBlock(), brInst.getFalseBlock())); // get a copy
                    retChange = true;
                }
            }
        }
        changed |= retChange;
        return retChange;
    }
}
