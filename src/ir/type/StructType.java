package ir.type;

import ir.IRVisitor;
import ir.operand.Operand;

import java.util.ArrayList;

public class StructType extends IRType {
    private String name;
    private ArrayList<IRType> memberList; // methods are also included.

    public StructType(String name, ArrayList<IRType> memberList) {
        this.name = "struct." + name;
        this.memberList = memberList;
    }

    public String getName() {
        return name;
    }

    public ArrayList<IRType> getMemberList() {
        return memberList;
    }

    private int align(int size, int base) {
        if (base == 0) return 0;
        if (size % base == 0) return size;
        else return size + base - (size % base);
    }

    public int getOffset(int index) {
        assert index >= 0 && index < memberList.size();
        int offset = 0;
        for (int i = 0; i <= index; ++i) {
            int typeSize = memberList.get(i).getBytes();
            offset = align(offset, typeSize) + (i == index ? 0 : typeSize);
        }
        return offset;
    }

    @Override
    public int getBytes() {
        int totalBytes = 0;
        int maxSize = 0;
        for(var member: memberList) {
            int typeSize = member.getBytes();
            totalBytes = align(totalBytes, typeSize) + typeSize;
            maxSize = Math.max(maxSize, typeSize);
        }
        totalBytes = align(totalBytes, maxSize);
        return totalBytes;
    }

    @Override
    public Operand getDefaultValue() {
        throw new RuntimeException();
    }

    @Override
    public String toString() {
        return "%" + name;
    }
}
