package ir;

import java.util.*;

public class CFGSimplifier extends IRPass {
    public CFGSimplifier(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        module.getFuncMap().values().forEach(this::runFunc);
        return changed;
    }

    @Override
    protected void runFunc(Function function) {
        HashSet<Block> blocks = new HashSet<>(function.getBlocks());
        while (!blocks.isEmpty()) {
            var block = blocks.iterator().next();
            blocks.remove(block);
            if (block.getPredecessors().isEmpty() && !block.getName().contains("entry")) {
                // not entry && no preds: just delete it
                function.removeBlock(block);
                changed = true;
                blocks.addAll(block.getSuccessors());
            } else if (block.getPredecessors().size() == 1 && block.getPredecessors().iterator().next().getSuccessors().size() == 1) {
                // only has one pred && this is the only suc of that pred: merge
                var pred = block.getPredecessors().iterator().next();
                block.mergeInto(pred);
                changed = true;
                blocks.addAll(block.getSuccessors());
            }
        }
    }
}
