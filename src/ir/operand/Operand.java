package ir.operand;

import ir.IRVisitor;
import ir.instruction.Inst;
import ir.type.IRType;

import java.util.Map;

abstract public class Operand {
    private Map<Inst, Integer> use;
    private IRType type;

    public Operand(IRType type) {
        this.type = type;
    }

    public IRType getType() {
        return type;
    }

    public String getName() { return null; }

    public void addUse(Inst inst) {
        if (!use.containsKey(inst))
            use.put(inst, 1);
        else
            use.replace(inst, use.get(inst) + 1);
    }

    public Map<Inst, Integer> getUse() {
        return use;
    }

    abstract public void accept(IRVisitor visitor);

    @Override
    abstract public String toString();
}
