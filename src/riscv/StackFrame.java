package riscv;

import riscv.operands.StackAddr;
import riscv.operands.register.VirtualRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StackFrame {
    private ASMFunction function;
    private Map<VirtualRegister, StackAddr> spillAddrMap;
    private ArrayList<StackAddr> selfParamAddrList;
    private Map<ASMFunction, ArrayList<StackAddr>> paramAddrMap;
    private int size;

    public StackFrame(ASMFunction function) {
        this.function = function;
        this.size = 0;
        selfParamAddrList = new ArrayList<>();
        paramAddrMap = new LinkedHashMap<>();
        spillAddrMap = new LinkedHashMap<>();
    }

    public void addSelfParamPos(StackAddr stackAddr) {
        selfParamAddrList.add(stackAddr);
    }

    public ArrayList<StackAddr> getSelfParamAddrList() {
        return selfParamAddrList;
    }

    public Map<ASMFunction, ArrayList<StackAddr>> getParamAddrMap() {
        return paramAddrMap;
    }

    public Map<VirtualRegister, StackAddr> getSpillAddrMap() {
        return spillAddrMap;
    }

    public void getAllAddr() {
        int maxSpilledParamSize = 0;
        for (ArrayList<StackAddr> paramAddrs : paramAddrMap.values()) {
            maxSpilledParamSize = Integer.max(maxSpilledParamSize, paramAddrs.size());
        }
        // maxSpilledParamSize + spilledSize
        size = maxSpilledParamSize + spillAddrMap.size();

        for (int i = 0; i < selfParamAddrList.size(); ++i) {
            var paramAddr = selfParamAddrList.get(i);
            paramAddr.setOffset((size + i) * 4);
        }
        int j = 0;
        for (StackAddr stackAddr : spillAddrMap.values()) {
            stackAddr.setOffset((j + maxSpilledParamSize) * 4);
            j++;
        }
        for (var parameters : paramAddrMap.values()) {
            for (int k = 0; k < parameters.size(); k++) {
                var stackLocation = parameters.get(k);
                stackLocation.setOffset(k * 4);
            }
        }
    }

    public int getSize() {
        return size;
    }
}
