package ir;

import ir.operand.GlobalVar;
import ir.operand.Parameter;
import ir.type.FunctionType;
import ir.type.*;
import util.type.StringType;
import util.type.TypeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Module {
    private Map<String, GlobalVar> globalVarMap;
    private Map<String, GlobalVar> constStringMap;
    private Map<String, IRFunction> funcMap;
    private Map<String, IRFunction> builtInFuncMap;
    private Map<String, StructType> structMap;

    public Module(TypeTable astTypeTable) {
        globalVarMap = new HashMap<>();
        constStringMap = new HashMap<>();
        funcMap = new HashMap<>();
        builtInFuncMap = new HashMap<>();
        structMap = new HashMap<>();

        IRType returnType;
        ArrayList<Parameter> parameters;
        IRFunction builtInFunc;

        // Add built-in functions.
        
        // void print(string str);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.StringType.getRawIRType(), "str"));
        builtInFunc = new IRFunction(this, "print", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // void println(string str);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.StringType.getRawIRType(), "str"));
        builtInFunc = new IRFunction(this, "println", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // void printInt(int n);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "n"));
        builtInFunc = new IRFunction(this, "printInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // void printlnInt(int n);
        returnType = new VoidType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "n"));
        builtInFunc = new IRFunction(this, "printlnInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // string getString();
        returnType = util.type.StringType.getRawIRType();
        parameters = new ArrayList<>();
        builtInFunc = new IRFunction(this, "getString", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // int getInt();
        returnType = util.type.IntType.getRawIRType();
        parameters = new ArrayList<>();
        builtInFunc = new IRFunction(this, "getInt", returnType, parameters);
        addBuiltInFunction(builtInFunc);

        // string toString(int i);
        returnType = util.type.StringType.getRawIRType();
        parameters = new ArrayList<>();
        parameters.add(new Parameter(util.type.IntType.getRawIRType(), "i"));
        builtInFunc = new IRFunction(this, "toString", returnType, parameters);
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

    public void addFunction(IRFunction function) {
        this.funcMap.put(function.getName(), function);
    }

    private void addBuiltInFunction(IRFunction function) {
        this.builtInFuncMap.put(function.getName(),function);
    }
    
    public GlobalVar getConstString(String key) {
        return constStringMap.get(key);
    }

    public GlobalVar getGlobalVar(String key) {
        return globalVarMap.get(key);
    }

    public IRFunction getFunction(String key) {
        return funcMap.get(key);
    }

    public IRFunction getBuiltInFunction(String key) {
        return builtInFuncMap.get(key);
    }

    public StructType getStructure(String key) {
        return structMap.get(key);
    }
}
