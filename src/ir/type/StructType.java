package ir.type;

import ir.operand.IROperand;

import java.util.ArrayList;

public class StructType extends IRType {
    private String name;
    private ArrayList<IRType> memberList; // methods are also included.

    public StructType(String name, ArrayList<IRType> memberList) {
        this.name = name;
        this.memberList = memberList;
    }

    public String getName() {
        return name;
    }

    public ArrayList<IRType> getMemberList() {
        return memberList;
    }

    @Override
    public int getBytes() {
        int totalBytes = 0;
        for(var member: memberList) {
            totalBytes += member.getBytes();
        }
        return totalBytes;
    }

    @Override
    public IROperand getDefaultValue() {
        throw new RuntimeException();
    }
}
