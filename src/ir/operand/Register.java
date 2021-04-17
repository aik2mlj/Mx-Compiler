package ir.operand;

import ir.IRVisitor;
import ir.instruction.Inst;
import ir.type.IRType;

import java.util.ArrayList;

public class Register extends Operand {
    private String name;
    private Inst defInst;

    public Register(IRType type, String name) {
        super(type);
        this.name = name;
    }

    public void setDefInst(Inst defInst) { // TODO: where to set DefInst?
        this.defInst = defInst;
    }

    public Inst getDefInst() {
        return defInst;
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return getType().toString() + " %" + name;
    }

    @Override
    public String toStringWithoutType() {
        return "%" + name;
    }

    public void replaceAllUseWith(Operand replaced) {
        ArrayList<Inst> tmpUse = new ArrayList<>(use.keySet());
        tmpUse.forEach(inst -> inst.replaceUse(this, replaced));
    }
}
