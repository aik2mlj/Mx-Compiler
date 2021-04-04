package riscv.operands;

abstract public class ASMOperand {
    abstract public String emit();

    @Override
    abstract public String toString();
}
