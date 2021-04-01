package riscv.instuctions;

import riscv.ASMBlock;
import riscv.operands.Address;
import riscv.operands.register.VirtualRegister;

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
    }

    public VirtualRegister getRd() {
        return rd;
    }

    public Address getAddr() {
        return addr;
    }

    public ByteSize getByteSize() {
        return byteSize;
    }

    @Override
    public String emit() {
        return "\t" + byteSize + "\t" + rd.emit() + ", " + addr.emit();
    }
}
