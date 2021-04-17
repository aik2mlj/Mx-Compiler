package ir.operand;

import ir.IRVisitor;
import ir.instruction.Inst;
import ir.type.IRType;

import java.util.HashMap;
import java.util.Map;

abstract public class Operand {
    protected Map<Inst, Integer> use;
    private IRType type;

    public Operand(IRType type) {
        this.type = type;
        use = new HashMap<>();
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

    public void removeUse(Inst inst) {
        if (use.containsKey(inst)) {
            if (use.get(inst) > 1)
                use.replace(inst, use.get(inst) - 1);
            else use.remove(inst);
        }
    }

    public Map<Inst, Integer> getUse() {
        return use;
    }

    abstract public void accept(IRVisitor visitor);

    @Override
    abstract public String toString();

    abstract public String toStringWithoutType();
}
