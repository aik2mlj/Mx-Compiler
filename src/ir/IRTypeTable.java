package ir;

import ir.type.IRType;
import ir.type.StructType;
import util.type.*;
import util.type.Type;
import util.type.TypeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IRTypeTable {
    private Module module;
    private Map<Type, IRType> typeTable;

    public IRTypeTable(Module module, TypeTable astTypeTable) {
        this.module = module;
        typeTable = new HashMap<>();
        typeTable.put(new IntType(), IntType.getRawIRType());
        typeTable.put(new BoolType(), BoolType.getRawIRType());
        typeTable.put(new StringType(), StringType.getRawIRType());
        typeTable.put(new VoidType(), VoidType.getRawIRType());
        // create StructType first
        for(var it: astTypeTable.getTypeTable().values()) {
            if(it instanceof ClassType) {
                var memberList = new ArrayList<IRType>();
                typeTable.put(it, new ir.type.StructType("class." + it.getTypeName(), memberList));
            }
        }
        // get memberList
        for(var astType: typeTable.keySet()) {
            if(astType instanceof ClassType) {
                ArrayList<IRType> memberList = ((StructType) typeTable.get(astType)).getMemberList();
                for(var member: ((ClassType) astType).getMembers()) {
                    Type memberType = astTypeTable.getType(member.getTypeNode());
                    IRType irType = memberType.getIRType(this);
                    memberList.add(irType);
                }
                StructType newStruct = new StructType("class." + astType.getTypeName(), memberList);
                module.addStructure(newStruct);
            }
        }
    }

    public IRType get(Type astType) {
        return this.typeTable.get(astType);
    }
}
