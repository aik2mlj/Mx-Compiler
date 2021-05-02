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

    public void decreaseOffset(int dec) {
        this.offset -= dec;
    }

    public int getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    @Override
    public String emit() {
        return offset + "(sp)";
    }

    @Override
    public String toString() {
        return offset + "(sp)";
    }
}
