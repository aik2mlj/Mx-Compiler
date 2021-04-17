package ir.operand;

import ir.instruction.Inst;
import ir.type.IRType;

abstract public class Constant extends Operand {
    public Constant(IRType type) {
        super(type);
    }

    @Override
    public void addUse(Inst inst) {
        // do nothing
    }

    @Override
    public void removeUse(Inst inst) {
        // do nothing
    }
}
