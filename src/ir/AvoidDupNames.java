package ir;

import ir.instruction.*;
import ir.operand.*;
import ir.type.*;
import riscv.instuctions.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AvoidDupNames implements IRVisitor {
    private Map<String, ArrayList<Block>> blockMap;
    private Map<String, ArrayList<Operand>> symbolMap;

    public AvoidDupNames(Module module) {
        blockMap = new HashMap<>();
        symbolMap = new HashMap<>();
        module.getFuncMap().values().forEach(function -> function.accept(this));
    }

    private void avoidDupName(Register register) {
        if (symbolMap.containsKey(register.getName())) {
            var regs = symbolMap.get(register.getName());
            register.rename(register.getName() + "." + regs.size());
            regs.add(register);
        } else {
            ArrayList<Operand> regs = new ArrayList<>();
            regs.add(register);
            symbolMap.put(register.getName(), regs);
        }
    }

    private void avoidDupName(Parameter param) {
        if (symbolMap.containsKey(param.getName())) {
            var regs = symbolMap.get(param.getName());
            param.rename(param.getName() + ".." + regs.size());
            regs.add(param);
        } else {
            ArrayList<Operand> regs = new ArrayList<>();
            regs.add(param);
            symbolMap.put(param.getName(), regs);
        }
    }

    @Override
    public void visit(Module module) {

    }

    @Override
    public void visit(Function function) {
//        if (function.getName().equals("__init__"))
//            return;
        function.getParameters().forEach(this::avoidDupName);
        function.getBlocks().forEach(block -> {
            if (blockMap.containsKey(block.getName())) {
                var blocks = blockMap.get(block.getName());
                block.rename(block.getName() + "." + blocks.size());
                blocks.add(block);
            } else {
                ArrayList<Block> blocks = new ArrayList<>();
                blocks.add(block);
                blockMap.put(block.getName(), blocks);
            }
            block.accept(this);
        });
    }

    @Override
    public void visit(Block block) {
        for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
            if (inst.hasDstReg() && !(inst instanceof MoveInst))
                avoidDupName(inst.getDstReg());
        }
    }

    @Override
    public void visit(AllocaInst inst) {

    }

    @Override
    public void visit(BinaryInst inst) {

    }

    @Override
    public void visit(BitcastToInst inst) {

    }

    @Override
    public void visit(BrInst inst) {

    }

    @Override
    public void visit(CallInst inst) {

    }

    @Override
    public void visit(GetElementPtrInst inst) {

    }

    @Override
    public void visit(IcmpInst inst) {

    }

    @Override
    public void visit(LoadInst inst) {

    }

    @Override
    public void visit(PhiInst inst) {

    }

    @Override
    public void visit(RetInst inst) {

    }

    @Override
    public void visit(StoreInst inst) {

    }

    @Override
    public void visit(MoveInst inst) {

    }
}
