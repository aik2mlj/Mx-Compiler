package riscv.operands.register;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PhysicalRegister extends Register {
    static public String[] prNames = {
            "zero", "ra", "sp", "gp", "tp",
            "t0", "t1", "t2", "s0", "s1",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
            "t3", "t4", "t5", "t6"
    };
    static public String[] argPRNames = {
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
    };
    static public String[] callerSavePRNames = {
            "ra", "t0", "t1", "t2",
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "t3", "t4", "t5", "t6"
    };
    static public String[] calleeSavePRNames = {
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11"
    };
    static public String[] allocatablePRNames = {
            // Except zero, sp, gp and tp.
            "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6",
            "ra"
    };

    static public String[] tmpPRNames = {
            "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6"
    };

    static public Map<String, PhysicalRegister> prs;
    static public Map<String, PhysicalRegister> callerSavePRs;
    static public Map<String, PhysicalRegister> calleeSavePRs;
    static public ArrayList<PhysicalRegister> tmpPRs;
    static public ArrayList<PhysicalRegister> allocatablePRs;

    // these vrs have fixed prs.
    static public Map<String, VirtualRegister> vrs;
    static public VirtualRegister zeroVR;
    static public VirtualRegister raVR;
    static public ArrayList<VirtualRegister> calleeSaveVRs;
    static public ArrayList<VirtualRegister> argVRs;

    static {
        prs = new LinkedHashMap<>();
        for (String prName : prNames) {
            prs.put(prName, new PhysicalRegister(prName));
        }
        callerSavePRs = new LinkedHashMap<>();
        for (String callerSavePRName : callerSavePRNames) {
            callerSavePRs.put(callerSavePRName, prs.get(callerSavePRName));
        }
        calleeSavePRs = new LinkedHashMap<>();
        for (String calleeSavePRName : calleeSavePRNames) {
            calleeSavePRs.put(calleeSavePRName, prs.get(calleeSavePRName));
        }
        // ------------
        tmpPRs = new ArrayList<>();
        for (String tmpPRName : tmpPRNames) {
            tmpPRs.add(prs.get(tmpPRName));
        }
        allocatablePRs = new ArrayList<>();
        for (String allocatablePRName: allocatablePRNames) {
            allocatablePRs.add(prs.get(allocatablePRName));
        }
        // ------------

        vrs = new LinkedHashMap<>();
        for (String prName : prNames) {
            VirtualRegister vr = new VirtualRegister(prName);
            vr.setColor(prs.get(prName)); // already colored
            vrs.put(prName, vr);
        }
        zeroVR = vrs.get("zero");
        raVR = vrs.get("ra");
        calleeSaveVRs = new ArrayList<>();
        for (String name : calleeSavePRNames)
            calleeSaveVRs.add(vrs.get(name));
        argVRs = new ArrayList<>();
        for (String name: argPRNames)
            argVRs.add(vrs.get(name));
    }

    private String name;

    public PhysicalRegister(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String emit() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
