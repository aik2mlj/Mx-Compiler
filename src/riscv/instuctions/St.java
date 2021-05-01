package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Address;
import riscv.operands.BaseOffsetAddr;
import riscv.operands.RelocationImm;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashSet;
import java.util.Set;

public class St extends ASMInst {
    public enum ByteSize {
        sb, sw
    }
    private ByteSize byteSize;
    private VirtualRegister rs;
    private Address addr;
    public St(ASMBlock parentBlock, ByteSize byteSize, VirtualRegister rs, Address addr) {
        super(parentBlock);
        this.byteSize = byteSize;
        this.rs = rs;
        this.addr = addr;

        this.rs.addUse(this);
        if (addr instanceof BaseOffsetAddr)
            ((BaseOffsetAddr) addr).getBase().addUse(this);
    }

    public ByteSize getByteSize() {
        return byteSize;
    }

    public Address getAddr() {
        return addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }

    public VirtualRegister getRs() {
        return rs;
    }

    @Override
    public Set<VirtualRegister> getUses() {
        Set<VirtualRegister> ret = new LinkedHashSet<>();
        ret.add(rs);
        if (addr instanceof BaseOffsetAddr) {
            ret.add(((BaseOffsetAddr) addr).getBase());
        }
        return ret;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        super.replaceUse(oldVR, newVR);
        boolean ok = false;
        if (rs == oldVR) { rs = newVR; ok = true; }
        if (addr instanceof BaseOffsetAddr && ((BaseOffsetAddr) addr).getBase() == oldVR) {
            ((BaseOffsetAddr) addr).setBase(newVR);
            ok = true;
        }
        if (!ok) throw new RuntimeException();
    }

    @Override
    public String emit() {
        return "\t" + byteSize + "\t" + rs.emit() + ", " + addr.emit();
    }

    @Override
    public String toString() {
        return byteSize + "\t" + rs.toString() + ", " + addr.toString();
    }
}
