package riscv.operands;

public class IntImm extends Immediate {
    private int value;

    public IntImm(int value) {
        this.value = value;
    }

    public void turnNegative() {
        this.value = -this.value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String emit() {
        return String.valueOf(value);
    }
}
