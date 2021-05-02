package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;

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
//                if (inst instanceof BinaryInst && ((BinaryInst) inst).getRhs() instanceof Constant) {
//                    Register dstReg = inst.getDstReg();
//                    while (inst.next instanceof BinaryInst && inst.canCombineNext()) {
//                        inst = inst.combineNext();
//                    }
//                }
                if (inst.hasDstReg() && inst.getDstReg().getUse().size() == 1 && inst.getDstReg().getUse().containsKey(inst.next)) {
                    var retPair = inst.combineNext();
                    changed |= retPair.getFirst();
                    inst = retPair.getSecond();
                } else
                    inst = inst.next;
            }
        }
    }
}
