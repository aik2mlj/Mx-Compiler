package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Address;
import riscv.operands.BaseOffsetAddr;
import riscv.operands.RelocationImm;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
import java.util.Set;

public class Ld extends ASMInst {
    public enum ByteSize {
        lb, lw
    }
    private VirtualRegister rd;
    private ByteSize byteSize;
    private Address addr;

    public Ld(ASMBlock parentBlock, ByteSize byteSize, VirtualRegister rd, Address addr) {
        super(parentBlock);
        this.rd = rd;
        this.byteSize = byteSize;
        this.addr = addr;

        this.rd.addDef(this);
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    public Address getAddr() {
        return addr;
    }

    public ByteSize getByteSize() {
        return byteSize;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new HashSet<>();
        if (addr instanceof BaseOffsetAddr) {
//            if (((BaseOffsetAddr) addr).getOffset() instanceof RelocationImm)
            ret.add(((BaseOffsetAddr) addr).getBase());
        }
        return ret;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(rd);
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
        if (rd == oldVR) rd = newVR;
        else throw new RuntimeException();
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        if (addr instanceof BaseOffsetAddr) {
            assert ((BaseOffsetAddr) addr).getBase() == oldVR;
            ((BaseOffsetAddr) addr).setBase(newVR);
        }
    }

    @Override
    public String emit() {
        return "\t" + byteSize + "\t" + rd.emit() + ", " + addr.emit();
    }

    @Override
    public String toString() {
        return byteSize + "\t" + rd.toString() + ", " + addr.toString();
    }
}
