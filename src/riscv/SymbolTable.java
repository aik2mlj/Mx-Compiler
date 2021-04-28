package riscv;

import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, VirtualRegister> vrMap;
    private Map<String, Integer> vrRenameCnt;
    private Map<String, ASMBlock> blockMap;

    public SymbolTable() {
        vrMap = new HashMap<>();
        blockMap = new HashMap<>();
        vrRenameCnt = new HashMap<>();
        // HOLY $H!T this thing gets me wrong all these time! Forgot to avoid duplicated names with Physical register!
        PhysicalRegister.vrs.values().forEach(this::addVR);
    }

    public void addVR(VirtualRegister vr) {
        if (vrMap.containsKey(vr.getName())) {
            int id = vrRenameCnt.get(vr.getName());
            vrRenameCnt.replace(vr.getName(), id + 1);
            vr.setName(vr.getName() + "." + id);
        } else {
            vrRenameCnt.put(vr.getName(), 0);
        }
        vrMap.put(vr.getName(), vr);
    }

    public void removeVR(VirtualRegister vr) {
        assert vrMap.containsKey(vr.getName());
        vrMap.remove(vr.getName());
    }

    public void addASMBlock(ASMBlock asmBlock) {
        blockMap.put(asmBlock.getIrName(), asmBlock);
    }

    public boolean containsVR(String vrName) {
        return vrMap.containsKey(vrName);
    }

    public VirtualRegister getVR(String vrName) {
        if (!vrMap.containsKey(vrName)) throw new RuntimeException();
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
