package util.type;

import ast.SingleTypeNode;
import ir.IRTypeTable;
import ir.operand.ConstNull;
import ir.operand.Operand;
import ir.type.IRType;
import util.Position;
import util.entity.FuncEntity;
import util.entity.VarEntity;

import java.util.ArrayList;

public class StringType extends BasicType {
    private ArrayList<FuncEntity> methods;

    public StringType() {
        super("string");

        // built-in methods
        methods = new ArrayList<>();
        ArrayList<VarEntity> params;
        // int length();
        Position pos = new Position(0, 0);
        FuncEntity length_func = new FuncEntity("length", pos, new SingleTypeNode(pos, "int"),
                new ArrayList<>(), null, FuncEntity.EntityType.Method);
        methods.add(length_func);

        // string substring(int left, int right);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("int", "left"));
        params.add(VarEntity.newBuiltInParam("int", "right"));
        FuncEntity substring_func = new FuncEntity("substring", pos, new SingleTypeNode(pos, "string"),
                params, null, FuncEntity.EntityType.Method);
        methods.add(substring_func);

        // int parseInt();
        FuncEntity parseInt_func = new FuncEntity("parseInt", pos, new SingleTypeNode(pos, "int"),
                new ArrayList<>(), null, FuncEntity.EntityType.Method);
        methods.add(parseInt_func);

        // int ord(int pos);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("int", "pos"));
        FuncEntity ord_func = new FuncEntity("ord", pos, new SingleTypeNode(pos, "int"),
                params, null, FuncEntity.EntityType.Method);
        methods.add(ord_func);
    }

    public FuncEntity getMethod(String name) {
        for(FuncEntity it: methods) {
            if(it.getName().equals(name)) return it;
        }
        return null;
    }

    @Override
    public IRType getIRType(IRTypeTable irTypeTable) {
        return irTypeTable.get(this);
    }

    static public IRType getRawIRType() {
        return new ir.type.PointerType(new ir.type.IntType(ir.type.IntType.BitWidth.i8));
    }

    @Override
    public Operand getDefaultValue() {
        return new ConstNull();
    }
}
