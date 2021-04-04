package riscv;

import riscv.operands.register.VirtualRegister;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, VirtualRegister> vrMap;
    private Map<String, ASMBlock> blockMap;

    public SymbolTable() {
        vrMap = new HashMap<>();
        blockMap = new HashMap<>();
    }

    public void addVR(VirtualRegister vr) {
        if (vrMap.containsKey(vr.getName())) {
            int id = 1;
            while (vrMap.containsKey(vr.getName() + "_" + id)) ++id;
            vr.setName(vr.getName() + "_" + id);
        }
        vrMap.put(vr.getName(), vr);
    }

    public void addASMBlock(ASMBlock asmBlock) {
        blockMap.put(asmBlock.getIrName(), asmBlock);
    }

    public VirtualRegister getVR(String vrName) {
        return vrMap.get(vrName);
    }

    public ASMBlock getASMBlock(String blockName) {
        return blockMap.get(blockName);
    }

    public Map<String, ASMBlock> getBlockMap() {
        return blockMap;
    }

    public Map<String, VirtualRegister> getVrMap() {
        return vrMap;
    }
}
