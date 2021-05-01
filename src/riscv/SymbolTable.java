package riscv;

import riscv.operands.register.PhysicalRegister;
import riscv.operands.register.VirtualRegister;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, VirtualRegister> vrMap;
    private Map<String, Integer> vrRenameCnt;
    private Map<String, ASMBlock> blockMap;

    public SymbolTable() {
        vrMap = new LinkedHashMap<>();
        blockMap = new LinkedHashMap<>();
        vrRenameCnt = new LinkedHashMap<>();
        // HOLY $H!T this thing gets me wrong all these time! Forgot to avoid duplicated names with Physical register!
        PhysicalRegister.vrs.values().forEach(this::addVR);
    }

    public void addVR(VirtualRegister vr) {
        if (vrMap.containsKey(vr.getName())) throw new RuntimeException();
        vrMap.put(vr.getName(), vr);
    }

    public void addVRRename(VirtualRegister vr) {
//        String bareName = vr.getName();
//        String regex = "\\.[0-9]+$";
//        bareName = bareName.replaceAll(regex, "");
//        if (vrRenameCnt.containsKey(bareName)) {
//            int id = vrRenameCnt.get(bareName);
//            vrRenameCnt.replace(bareName, id + 1);
//            vr.setName(bareName + "." + id);
//        } else {
//            vrRenameCnt.put(bareName, 0);
//            vr.setName(bareName);
//        }
//        if (vrMap.containsKey(vr.getName())) {
            int id = 0;
            while (vrMap.containsKey(vr.getName() + "." + id)) ++id;
            vr.setName(vr.getName() + "." + id);
//        }
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
