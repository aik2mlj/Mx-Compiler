package riscv.operands;

import riscv.operands.register.VirtualRegister;

public class BaseOffsetAddr extends Address {
    private VirtualRegister base;
    private Immediate offset;

    public BaseOffsetAddr(VirtualRegister base, Immediate offset) {
        this.base = base;
        this.offset = offset;
    }

    public VirtualRegister getBase() {
        return base;
    }

    public void setBase(VirtualRegister base) {
        this.base = base;
    }

    public Immediate getOffset() {
        return offset;
    }

    @Override
    public String emit() {
        return offset.emit() + "(" + base.emit() + ")";
    }

    @Override
    public String toString() {
        return offset.toString() + "(" + base.toString() + ")";
    }
}
