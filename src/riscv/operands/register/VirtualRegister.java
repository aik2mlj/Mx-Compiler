package riscv.operands.register;

public class VirtualRegister extends Register {
    private String name;
    private PhysicalRegister trueReg;

    public VirtualRegister(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTrueReg(PhysicalRegister trueReg) {
        this.trueReg = trueReg;
    }

    @Override
    public String emit() {
        return trueReg.emit();
    }
}
