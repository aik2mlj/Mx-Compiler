package ir;

import ir.operand.ConstString;
import ir.operand.GlobalVar;
import ir.operand.Parameter;
import ir.type.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Module {
    private final Map<String, GlobalVar> globalVarMap;
    private final Map<String, GlobalVar> constStringMap;
    private final Map<String, Function> funcMap;
    private final Map<String, Function> builtInFuncMap;
    private final Map<String, StructType> structMap;

    private final LinkedHashSet<Function> IOBuiltInFunc;

    public Module() {
        globalVarMap = new LinkedHashMap<>();
        constStringMap = new LinkedHashMap<>();
        funcMap = new LinkedHashMap<>();
        builtInFuncMap = new LinkedHashMap<>();
        structMap = new LinkedHashMap<>();

        IOBuiltInFunc = new LinkedHashSet<>();

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
        IOBuiltInFunc.add(builtInFunc);

        // void println(string str);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.StringType.getRawIRType(), "str"));
        builtInFunc = new Function(this, "println", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        IOBuiltInFunc.add(builtInFunc);

        // void printInt(int n);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "n"));
        builtInFunc = new Function(this, "printInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        IOBuiltInFunc.add(builtInFunc);

        // void printlnInt(int n);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "n"));
        builtInFunc = new Function(this, "printlnInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        IOBuiltInFunc.add(builtInFunc);

        // string getString();
        returnType = util.type.StringType.getRawIRType();
        parameters = new ArrayList<>();
        builtInFunc = new Function(this, "getString", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        IOBuiltInFunc.add(builtInFunc);

        // int getInt();
        returnType = util.type.IntType.getRawIRType();
        parameters = new ArrayList<>();
        builtInFunc = new Function(this, "getInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        IOBuiltInFunc.add(builtInFunc);

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
        builtInFunc.setSideEffect(true);
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
        // string string.add(string str1, string str2);
        returnType = new PointerType(new IntType(IntType.BitWidth.i8));
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_add", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.eq(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_eq", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.ne(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_ne", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.lt(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_lt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.gt(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_gt", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.le(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_le", returnType, parameters);
        addBuiltInFunction(builtInFunc);
        

        // bool string.ge(string str1, string str2);
        returnType = new IntType(IntType.BitWidth.i1);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str1"));
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "str2"));
        builtInFunc = new Function(this, "_string_ge", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // int array.size(array arr);
        returnType = new IntType(IntType.BitWidth.i32);
        parameters = new ArrayList<>();
        parameters.add(new Parameter(new PointerType(new IntType(IntType.BitWidth.i8)), "arr"));
        builtInFunc = new Function(this, "_array_size", returnType, parameters);
        addBuiltInFunction(builtInFunc);
    }

    public void addStructure(StructType structType) {
        this.structMap.put(structType.getName(), structType);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVarMap.put(globalVar.getName(), globalVar);
    }

    public GlobalVar addConstString(String str) {
        str = str.replace("\\\\", "\\");
        str = str.replace("\\n", "\n");
        str = str.replace("\\\"", "\"");
        str = str + "\0";
        if (constStringMap.containsKey(str))
            return constStringMap.get(str);
        else {
            String name = ".str" + constStringMap.size();
            GlobalVar strVar = new GlobalVar(new PointerType(new ir.type.ArrayType(str.length(),
                    new IntType(IntType.BitWidth.i8))), name, new ConstString(str));
            constStringMap.put(str, strVar);
            globalVarMap.put(name, strVar);
            return strVar;
        }
    }

    public void addFunction(Function function) {
        this.funcMap.put(function.getName(), function);
    }

    private void addBuiltInFunction(Function function) {
        this.builtInFuncMap.put(function.getName(),function);
    }

    public Function getFunction(String key) {
        return funcMap.get(key);
    }

    public Function getBuiltInFunction(String key) {
        return builtInFuncMap.get(key);
    }

    public Map<String, GlobalVar> getGlobalVarMap() {
        return globalVarMap;
    }

    public Map<String, Function> getFuncMap() {
        return funcMap;
    }

    public Map<String, StructType> getStructMap() {
        return structMap;
    }

    public Map<String, Function> getBuiltInFuncMap() {
        return builtInFuncMap;
    }

    public LinkedHashSet<Function> getIOBuiltInFunc() {
        return IOBuiltInFunc;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
