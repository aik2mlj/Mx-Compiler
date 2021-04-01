package riscv;

import ir.Block;
import ir.Function;
import ir.instruction.Inst;
import riscv.operands.BaseOffsetAddr;
import riscv.operands.register.VirtualRegister;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ASMFunction {
    private ASMModule module;
    private String name;
    private Function irFunction;

    private ASMBlock entryBlock, exitBlock;
    private LinkedList<ASMBlock> blocks;

    private StackFrame stackFrame;
    private SymbolTable symbolTable;
    private Map<VirtualRegister, BaseOffsetAddr> gepAddrMap;

    public ASMFunction(ASMModule module, String name, Function irFunction) {
        this.module = module;
        this.name = name;
        this.irFunction = irFunction;

        this.blocks = new LinkedList<>();
        this.entryBlock = this.exitBlock = null;

        this.symbolTable = new SymbolTable();
        this.gepAddrMap = new HashMap<>();

        int functionCnt = module.getFuncMap().size();
        int cnt = 0;
        for (Block irBlock : irFunction.getBlocks()) {
            var newASMBlock = new ASMBlock(this, irBlock.getName(), ".LBB" + functionCnt + "_" + ++cnt);
            this.appendBlock(newASMBlock);
            irBlock.setAsmBlock(newASMBlock);
        }
        // build all blocks
        for (int i = 0; i < irFunction.getBlocks().size(); ++i) {
            var irBlock = irFunction.getBlocks().get(i);
            var block = blocks.get(i);
            Set<ASMBlock> predecessors = block.getPredecessors();
            Set<ASMBlock> successors = block.getSuccessors();
            for (Block irBlockPredecessor : irBlock.getPredecessors()) {
                predecessors.add(irBlockPredecessor.getAsmBlock());
            }
            for (Block irBlockSuccessor : irBlock.getSuccessors()) {
                successors.add(irBlockSuccessor.getAsmBlock());
            }
        }
        entryBlock = blocks.getFirst();
        exitBlock = blocks.getLast();
        // add params and dstReg to symbolTable
        for (var parameter : irFunction.getParameters()) {
            VirtualRegister vr = new VirtualRegister(parameter.getName());
            symbolTable.addVR(vr);
        }
        for (Block irBlock : irFunction.getBlocks()) {
            for (Inst irInst : irBlock.getInsts()) {
                if (irInst.hasDstReg()) {
                    String regName = irInst.getDstReg().getName();
                    // FIXME: MoveInst is different?
                    var vr = new VirtualRegister(regName);
                    symbolTable.addVR(vr);
                }
            }
        }
    }

    public ASMModule getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public ASMBlock getEntryBlock() {
        return entryBlock;
    }

    public void setEntryBlock(ASMBlock entryBlock) {
        assert blocks.contains(entryBlock);
        this.entryBlock = entryBlock;
    }

    public void setStackFrame(StackFrame stackFrame) {
        this.stackFrame = stackFrame;
    }

    public StackFrame getStackFrame() {
        return stackFrame;
    }

    public void appendBlock(ASMBlock newBlock) {
        blocks.add(newBlock);
        if(entryBlock == null)
            entryBlock = newBlock;
        exitBlock = newBlock;
    }

    public Map<VirtualRegister, BaseOffsetAddr> getGepAddrMap() {
        return gepAddrMap;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
