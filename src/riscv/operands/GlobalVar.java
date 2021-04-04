package riscv.operands;

import riscv.ASMVisitor;

public class GlobalVar extends ASMOperand {
    public enum VarType {
        STRING, BOOL, INT
    }

    private String name;

    private VarType varType;
    private String stringValue;
    private boolean boolValue;
    private int intValue;

    public GlobalVar(String name) {
        this.name = name;
    }

    public void setString(String value) {
        this.varType = VarType.STRING;
        stringValue = value;
    }

    public void setBool(boolean value) {
        this.varType = VarType.BOOL;
        boolValue = value;
    }

    public void setInt(int value) {
        this.varType = VarType.INT;
        intValue = value;
    }

    public VarType getVarType() {
        return varType;
    }

    public String getName() {
        return name;
    }

    public void accept(ASMVisitor visitor) { visitor.visit(this); }

    @Override
    public String emit() {
        switch (varType) {
            case STRING -> {
                String res = stringValue.replace("\\", "\\\\");
                res = res.replace("\n", "\\n");
                res = res.replace("\"", "\\\"");
                return "\t.asciz\t\"" + res + "\"";
            }
            case BOOL -> {
                return "\t.byte\t" + (boolValue? "1": "0") + " ".repeat(36 - 13) + "# " + (boolValue);
            }
            default -> { // INT
                return "\t.word\t" + Integer.toUnsignedLong(intValue)
                        + " ".repeat(24 - Integer.toUnsignedString(intValue).length()) + "# " + intValue;
            }
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
