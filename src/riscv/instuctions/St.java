package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Address;
import riscv.operands.BaseOffsetAddr;
import riscv.operands.RelocationImm;
import riscv.operands.register.VirtualRegister;

import java.util.HashSet;
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
        Set<VirtualRegister> ret = new HashSet<>();
        ret.add(rs);
        if (addr instanceof BaseOffsetAddr) {
            ret.add(((BaseOffsetAddr) addr).getBase());
        }
        return ret;
    }

    @Override
    public Set<VirtualRegister> getDefs() {
        return null;
    }

    @Override
    public void replaceDef(VirtualRegister oldVR, VirtualRegister newVR) {
    }

    @Override
    public void replaceUse(VirtualRegister oldVR, VirtualRegister newVR) {
        if (rs == oldVR) rs = newVR;
        else if (addr instanceof BaseOffsetAddr) {
            assert ((BaseOffsetAddr) addr).getBase() == oldVR;
            ((BaseOffsetAddr) addr).setBase(newVR);
        }
        else throw new RuntimeException();
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
