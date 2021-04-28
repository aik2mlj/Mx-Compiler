package ir;

import ir.instruction.Inst;
import ir.instruction.MoveInst;
import ir.instruction.PhiInst;
import ir.operand.Operand;
import ir.operand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ResolvePhi extends IRPass {
    public class ParallelCopy extends Inst {
        public ArrayList<MoveInst> copies = new ArrayList<>();
        public HashMap<Operand, Integer> uses = new HashMap<>();

        public ParallelCopy(Block parentBlock) {
            super(parentBlock);
        }

        public void addMove(MoveInst inst) {
            copies.add(inst);
            var src = inst.getSrc();
            if (src instanceof Register) {
                if (uses.containsKey(src))
                    uses.replace(src, uses.get(src) + 1);
                else uses.put(src, 1);
            }
        }

        @Override
        public void removeUse() {

        }

        @Override
        public void addUseAndDef() {

        }

        @Override
        public void accept(IRVisitor visitor) {
            // won't be visited
        }

        @Override
        public void replaceUse(Register original, Operand replaced) {

        }

        @Override
        public String toString() {
            return "paraCopy in " + getParentBlock().getName();
        }

        public MoveInst findValidMove() {
            for (MoveInst move : copies) {
                boolean flag = true;
                for (MoveInst move1 : copies) {
                    if (move.getDstReg() == move1.getSrc()) {
                        flag = false;
                        break;
                    }
                }
                if (flag) return move;
            }
            return null;
        }
    }

    public ResolvePhi(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        module.getFuncMap().values().forEach(this::runFunc);
        return false;
    }

    @Override
    protected void runFunc(Function function) {
        // split critical edges
        for (Block block : function.getDFSBlocks()) {
            var preds = new HashSet<>(block.getPredecessors());
            if (preds.size() == 1) {
                var phis = new ArrayList<>(block.getPhiInsts());
                for (PhiInst phiInst : phis) {
                    phiInst.getDstReg().replaceAllUseWith(phiInst.getValues().get(0));
                }
                continue;
            }
            for (Block pred : preds) {
                if (pred.getSuccessors().size() > 1) {
                    // insert mid block
                    Block midBlock = new Block(function, "splitmid");
                    ParallelCopy newPC = new ParallelCopy(midBlock);
                    midBlock.appendInst(newPC);
                    midBlock.setParallelCopy(newPC);
                    midBlock.appendBrInstTo_U(block);
                    function.appendBlock(midBlock); // FIXME: add in middle?
                    pred.replaceSuc(block, midBlock);
                    // replace phiInsts
                    block.getPhiInsts().forEach(phiInst -> phiInst.replaceBlock(pred, midBlock));
                } else {
                    // add before tailInst
                    ParallelCopy newPC = new ParallelCopy(pred);
                    pred.getTailInst().addPrev(newPC);
                    pred.setParallelCopy(newPC);
                }
            }
//            new IRPrinter("SSAcout__.ll", module);

            var phis = new ArrayList<>(block.getPhiInsts());
            for (PhiInst phiInst : phis) {
                for (int i = 0; i < phiInst.getValues().size(); ++i) {
                    var pred = phiInst.getPredecessors().get(i);
                    var value = phiInst.getValues().get(i);
                    pred.getParallelCopy().addMove(new MoveInst(pred, phiInst.getDstReg(), value));
                }
                phiInst.removeFromBlock();
            }
        }

        // sequentialize
        for (Block block : function.getBlocks()) {
            var pc = block.getParallelCopy();
            if (pc == null) continue;
            ArrayList<MoveInst> seq = new ArrayList<>();
            var pcopy = pc.copies;
            while (!pcopy.isEmpty()) {
                var move = pc.findValidMove();
                if (move != null) {
                    seq.add(move);
                    pcopy.remove(move);
                } else { // pcopy is only made of cycles, break one of them
                    move = pcopy.get(0);
                    var src = move.getSrc();
                    Register cycle = new Register(src.getType(), "breakcycle", function);
                    seq.add(new MoveInst(block, cycle, src));
                    move.setSrc(cycle);
                }
            }
            seq.forEach(moveInst -> block.getTailInst().addPrev(moveInst));
            pc.removeFromBlock();
        }
    }
}
