package ir;

import ir.operand.GlobalVar;
import ir.operand.Parameter;
import ir.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Module {
    private final Map<String, GlobalVar> globalVarMap;
    private final Map<String, GlobalVar> constStringMap;
    private final Map<String, Function> funcMap;
    private final Map<String, Function> builtInFuncMap;
    private final Map<String, StructType> structMap;

    public Module() {
        globalVarMap = new HashMap<>();
        constStringMap = new HashMap<>();
        funcMap = new HashMap<>();
        builtInFuncMap = new HashMap<>();
        structMap = new HashMap<>();

        IRType returnType;
        ArrayList<Parameter> parameters;
        Function builtInFunc;

        // Add built-in functions.
        
        // void print(string str);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.StringType.getRawIRType(), "str"));
        builtInFunc = new Function(this, "print", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // void println(string str);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.StringType.getRawIRType(), "str"));
        builtInFunc = new Function(this, "println", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // void printInt(int n);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "n"));
        builtInFunc = new Function(this, "printInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // void printlnInt(int n);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "n"));
        builtInFunc = new Function(this, "printlnInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // string getString();
        returnType = util.type.StringType.getRawIRType();
        parameters = new ArrayList<>();
        builtInFunc = new Function(this, "getString", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // int getInt();
        returnType = util.type.IntType.getRawIRType();
        parameters = new ArrayList<>();
        builtInFunc = new Function(this, "getInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // string toString(int i);
        returnType = util.type.StringType.getRawIRType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "i"));
        builtInFunc = new Function(this, "toString", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // added ones
        // i8* malloc(i32 size)
        returnType = new PointerType(new IntType(IntType.BitWidth.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "size"));
        builtInFunc = new Function(this, "malloc", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // int string.length(string str);
        returnType = util.type.IntType.getRawIRType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str"));
        builtInFunc = new Function(this, "_string_length", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // string string.substring(string str, int left, int right);
        returnType = new PointerType(new IntType(IntType.BitWidth.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str"));
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "left"));
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "right"));
        builtInFunc = new Function(this, "_string_substring", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // int string.parseInt(string str);
        returnType = util.type.IntType.getRawIRType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str"));
        builtInFunc = new Function(this, "_string_parseInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // int ord(string str, int pos);
        returnType = util.type.IntType.getRawIRType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str"));
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "pos"));
        builtInFunc = new Function(this, "_string_ord", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // string binary ops
        // string string.concatenate(string str1, string str2);
        returnType = new PointerType(new IntType(IntType.BitWidth.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_add", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.equal(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_eq", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.notEqual(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_ne", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.lessThan(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_lt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.greaterThan(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_gt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.lessEqual(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_le", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.greaterEqual(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_ge", returnType, parameters);
        addBuiltInFunction(builtInFunc);
    }

    public void addStructure(StructType structType) {
        this.structMap.put(structType.getName(), structType);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVarMap.put(globalVar.getName(), globalVar);
    }

    public void addConstString(GlobalVar constString) {
        this.constStringMap.put(constString.getName(), constString);
    }

    public void addFunction(Function function) {
        this.funcMap.put(function.getName(), function);
    }

    private void addBuiltInFunction(Function function) {
        this.builtInFuncMap.put(function.getName(),function);
    }
    
    public GlobalVar getConstString(String key) {
        return constStringMap.get(key);
    }

    public GlobalVar getGlobalVar(String key) {
        return globalVarMap.get(key);
    }

    public Function getFunction(String key) {
        return funcMap.get(key);
    }

    public Function getBuiltInFunction(String key) {
        return builtInFuncMap.get(key);
    }

    public StructType getStructure(String key) {
        return structMap.get(key);
    }

    public Map<String, GlobalVar> getGlobalVarMap() {
        return globalVarMap;
    }

    public Map<String, GlobalVar> getConstStringMap() {
        return constStringMap;
    }

    public Map<String, Function> getFuncMap() {
        return funcMap;
    }

    public Map<String, StructType> getStructMap() {
        return structMap;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
