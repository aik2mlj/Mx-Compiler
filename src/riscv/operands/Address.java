package riscv.operands;

abstract public class Address {
    abstract public String emit();

    @Override
    abstract public String toString();
}
