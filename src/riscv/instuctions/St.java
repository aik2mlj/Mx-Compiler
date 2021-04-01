package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Address;
import riscv.operands.register.VirtualRegister;

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
    }

    public ByteSize getByteSize() {
        return byteSize;
    }

    public Address getAddr() {
        return addr;
    }

    public VirtualRegister getRs() {
        return rs;
    }

    @Override
    public String emit() {
        return "\t" + byteSize + "\t" + rs.emit() + ", " + addr.emit();
    }
}
