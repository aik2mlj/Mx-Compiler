package riscv;

import ir.Function;
import riscv.operands.GlobalVar;

import java.util.HashMap;
import java.util.Map;

public class ASMModule {
    private Map<String, ASMFunction> funcMap;
    private Map<String, ASMFunction> builtInFuncMap;
    private Map<String, GlobalVar> globalVarMap;

    public ASMModule() {
        funcMap = new HashMap<>();
        builtInFuncMap = new HashMap<>();
        globalVarMap = new HashMap<>();
    }

    public void addFunction(ASMFunction asmFunction) {
        funcMap.put(asmFunction.getName(), asmFunction);
    }

    public void addBuiltInFunc(ASMFunction builtInFunc) {
        builtInFuncMap.put(builtInFunc.getName(), builtInFunc);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        globalVarMap.put(globalVar.getName(), globalVar);
    }

    public GlobalVar getGlobalVar(String key) {
        return globalVarMap.get(key);
    }

    public ASMFunction getFunction(String key) {
        return funcMap.get(key);
    }

    public ASMFunction getBuiltInFunction(String key) {
        return builtInFuncMap.get(key);
    }

    public Map<String, GlobalVar> getGlobalVarMap() {
        return globalVarMap;
    }

    public Map<String, ASMFunction> getBuiltInFuncMap() {
        return builtInFuncMap;
    }

    public Map<String, ASMFunction> getFuncMap() {
        return funcMap;
    }
}
