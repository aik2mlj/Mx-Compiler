package ir;

import ir.instruction.BinaryInst;
import ir.instruction.MoveInst;
import ir.operand.ConstInt;

public class Move2Addi {
    private Module module;

    public Move2Addi(Module module) {
        this.module = module;
    }

    public void run() {
        module.getFuncMap().values().forEach(this::runFunc);
    }

    private void runFunc(Function function) {
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (inst instanceof MoveInst) {

                }
            }
        }
    }
}
