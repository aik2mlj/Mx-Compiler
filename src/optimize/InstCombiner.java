package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.BinaryInst;

public class InstCombiner extends IRPass {
    public InstCombiner(Module module) {
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
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null;) {
                var next = inst.next;
                if (inst instanceof BinaryInst) {

                }
                inst = next;
            }
        }
    }
}
