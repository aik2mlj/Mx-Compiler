package riscv;

import riscv.operands.StackAddr;

import java.util.ArrayList;
import java.util.Map;

public class StackFrame {
    private ASMFunction function;
    private ArrayList<StackAddr> selfParamPosList;
    private Map<ASMFunction, ArrayList<StackAddr>> paramPosMap;
    private int size;

    public StackFrame(ASMFunction function) {
        this.function = function;
        this.size = 0;
    }

    public void addSelfParamPos(StackAddr stackAddr) {
        selfParamPosList.add(stackAddr);
    }

    public ArrayList<StackAddr> getSelfParamPosList() {
        return selfParamPosList;
    }

    public Map<ASMFunction, ArrayList<StackAddr>> getParamPosMap() {
        return paramPosMap;
    }
}
