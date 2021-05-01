package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.BitcastToInst;
import ir.instruction.GetElementPtrInst;
import ir.instruction.LoadInst;
import ir.instruction.StoreInst;
import ir.operand.GlobalVar;
import ir.operand.Operand;
import ir.operand.Register;
import ir.type.PointerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class SillyAlias extends IRPass {
    private HashMap<Operand, Operand> effectMap;

    public SillyAlias(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        changed = false;
        module.getFuncMap().values().forEach(this::runFunc);
        return changed;
    }

    @Override
    protected void runFunc(Function function) {
        effectMap = new HashMap<>();
        HashSet<Operand> outerOps = new LinkedHashSet<>(module.getGlobalVarMap().values()); // outside values that may change
        function.getParameters().forEach(parameter -> {
            if (parameter.getType() instanceof PointerType) // if a param is a pointer, it will influence outside as well.
                outerOps.add(parameter);
        });
        // get all registers that may effect outerOps
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (inst instanceof GetElementPtrInst) {
                    putEffect(inst.getDstReg(), ((GetElementPtrInst) inst).getPointer(), outerOps);
                } else if (inst instanceof BitcastToInst) {
                    putEffect(inst.getDstReg(), ((BitcastToInst) inst).getSrc(), outerOps);
                } else if (inst instanceof LoadInst && inst.getDstReg().getType() instanceof PointerType) {
                    putEffect(inst.getDstReg(), ((LoadInst) inst).getPointer(), outerOps);
                }
            }
        }
        function.getAffectedOps().clear();
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (inst instanceof StoreInst) {
                    if (outerOps.contains(((StoreInst) inst).getPointer()))
                        function.getAffectedOps().add(((StoreInst) inst).getPointer());
                    else if (effectMap.containsKey(((StoreInst) inst).getPointer()))
                        function.getAffectedOps().add(effectMap.get(((StoreInst) inst).getPointer()));
                }
            }
        }
    }

    private void putEffect(Register dstReg, Operand pointer, HashSet<Operand> outerOps) {
        if (outerOps.contains(pointer)) {
            effectMap.put(dstReg, pointer);
        } else {
            Operand runner = pointer;
            while (effectMap.containsKey(runner)) runner = effectMap.get(runner);
            if (outerOps.contains(runner)) {
                effectMap.put(dstReg, runner);
            }
        }
    }
}
