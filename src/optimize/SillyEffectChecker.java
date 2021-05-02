package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;
import ir.instruction.BitcastToInst;
import ir.instruction.GetElementPtrInst;
import ir.instruction.LoadInst;
import ir.instruction.StoreInst;
import ir.operand.Operand;
import ir.operand.Parameter;
import ir.operand.Register;
import ir.type.PointerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class SillyEffectChecker extends IRPass {
    private HashMap<Operand, Operand> effectMap;

    public SillyEffectChecker(Module module) {
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
        function.getAffectedParamIndices().clear();
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (inst instanceof StoreInst) {
                    if (outerOps.contains(((StoreInst) inst).getPointer())) {

                        addAffected(function, ((StoreInst) inst).getPointer());
                    }
                    else if (effectMap.containsKey(((StoreInst) inst).getPointer())) {
                        addAffected(function, effectMap.get(((StoreInst) inst).getPointer()));
                    }
                }
            }
        }
        function.getLoadedOps().clear();
        function.getLoadedParamIndices().clear();
        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
                if (inst instanceof LoadInst) {
                    if (outerOps.contains(((LoadInst) inst).getPointer()))
                        addLoaded(function, ((LoadInst) inst).getPointer());
                } else if (inst instanceof GetElementPtrInst) {
                    if (outerOps.contains(((GetElementPtrInst) inst).getPointer()))
                        addLoaded(function, ((GetElementPtrInst) inst).getPointer());
                }
            }
        }
    }

    private void addLoaded(Function function, Operand operand) {
        if (operand instanceof Parameter) {
            int index = function.getParameters().indexOf(operand);
            if (index == -1) throw new RuntimeException();
            function.getLoadedParamIndices().add(index);
        } else
            function.getLoadedOps().add(operand);
    }

    private void addAffected(Function function, Operand operand) {
        if (operand instanceof Parameter) {
            int index = function.getParameters().indexOf(operand);
            if (index == -1) throw new RuntimeException();
            function.getAffectedParamIndices().add(index);
        } else
            function.getAffectedOps().add(operand);
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
