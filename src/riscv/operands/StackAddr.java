package riscv.operands;

public class StackAddr extends Address {
    private String name;
    private int offset;

    public StackAddr(String name) {
        this.name = name;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    @Override
    public String emit() {
        return offset + "(sp)";
    }
}
