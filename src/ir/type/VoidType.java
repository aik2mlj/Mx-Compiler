package ir.type;

import ir.IRVisitor;
import ir.operand.IROperand;

public class VoidType extends IRType {
    @Override
    public int getBytes() {
        return 0;
    }

    @Override
    public IROperand getDefaultValue() {
        throw new RuntimeException();
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
