package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.Inst;

import java.util.ArrayList;

public class CSE extends IRPass {
    public CSE(Module module) {
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
            ArrayList<Inst> qualified = new ArrayList<>();
            for (var inst = block.getHeadInst(); inst != null;) {
                var next = inst.next;
                boolean replaced = false;
                for (Inst q : qualified) {
                    if (inst.sameMeaning(q)) {
                        inst.getDstReg().replaceAllUseWith(q.getDstReg());
                        inst.removeFromBlock();
                        replaced = true;
                        break;
                    }
                }
                if (!replaced)
                    qualified.add(inst);
                changed |= replaced;
                inst = next;
            }
        }
    }
}
