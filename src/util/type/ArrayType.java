package util.type;

import ast.SingleTypeNode;
import ir.IRTypeTable;
import ir.type.IRType;
import ir.type.PointerType;
import util.Position;
import util.entity.FuncEntity;

import java.util.ArrayList;

public class ArrayType extends Type {
    private Type baseType;
    private int dimension;
    private ArrayList<FuncEntity> methods; // build_in size()

    public ArrayType(Type baseType, int dimension) {
        super(baseType.getTypeName() + "*" + dimension);
        this.baseType = baseType;
        this.dimension = dimension;

        // build_in size() method
        methods = new ArrayList<>();
        Position pos = new Position(0, 0);
        FuncEntity size_func = new FuncEntity("size", pos, new SingleTypeNode(pos, "int"),
                new ArrayList<>(), null, FuncEntity.EntityType.Method);
        methods.add(size_func);
    }

    public Type getBaseType() {
        return baseType;
    }

    public int getDimension() {
        return dimension;
    }

    public FuncEntity getMethod(String name) {
        for(FuncEntity it: methods) {
            if(it.getName().equals(name)) return it;
        }
        return null;
    }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        // return PointerType
        var irType = baseType.getIRType(irTypeTable);
        for(int i = 0; i < dimension; ++i)
            irType = new PointerType(irType);
        return irType;
    }
}
