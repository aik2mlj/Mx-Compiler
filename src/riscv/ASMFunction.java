package riscv;

import ir.Block;
import ir.Function;
import ir.instruction.Inst;
import ir.instruction.MoveInst;
import riscv.operands.BaseOffsetAddr;
import riscv.operands.register.VirtualRegister;

import java.util.*;

public class ASMFunction {
    private ASMModule module;
    private String name;
    private Function irFunction;
    private ArrayList<VirtualRegister> params;

    private ASMBlock entryBlock, exitBlock;
    private LinkedList<ASMBlock> blocks;

    private StackFrame stackFrame;
    private SymbolTable symbolTable;
    private Map<VirtualRegister, BaseOffsetAddr> gepAddrMap;

    public ASMFunction(ASMModule module, String name, Function irFunction) {
        this.module = module;
        this.name = name;
        this.irFunction = irFunction;
        this.params = new ArrayList<>();

        this.blocks = new LinkedList<>();
        this.entryBlock = this.exitBlock = null;

        this.symbolTable = new SymbolTable();
        this.gepAddrMap = new HashMap<>();

        int functionCnt = module.getFuncMap().size();
        int cnt = 0;
        for (Block irBlock : irFunction.getBlocks()) {
            var newASMBlock = new ASMBlock(this, irBlock.getName(), ".LBBB" + functionCnt + "_" + cnt++);
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
        // add params and dstReg to symbolTable
        for (var parameter : irFunction.getParameters()) {
            VirtualRegister vr = new VirtualRegister(parameter.getName());
            symbolTable.addVR(vr);
            params.add(vr);
        }
        for (Block irBlock : irFunction.getBlocks()) {
            for (Inst irInst = irBlock.getHeadInst(); irInst != null; irInst = irInst.next) {
                if (irInst.hasDstReg()) {
                    String regName = irInst.getDstReg().getName();
                    if (irInst instanceof MoveInst) {
                        if (symbolTable.getVR(regName) == null) {
                            var vr = new VirtualRegister(regName);
                            symbolTable.addVR(vr);
                        }
                    } else {
                        var vr = new VirtualRegister(regName);
                        symbolTable.addVR(vr);
                    }
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

    public ArrayList<VirtualRegister> getParams() {
        return params;
    }

    public LinkedList<ASMBlock> getBlocks() {
        return blocks;
    }

    public void setStackFrame(StackFrame stackFrame) {
        this.stackFrame = stackFrame;
    }

    public StackFrame getStackFrame() {
        return stackFrame;
    }

    public void appendBlock(ASMBlock newBlock) {
//        System.err.println(newBlock.getIrName() + ", " + newBlock.getName());
        symbolTable.addASMBlock(newBlock);
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

    public ArrayList<ASMBlock> getDFSBlocks() {
        ArrayList<ASMBlock> ret = new ArrayList<>();
        HashSet<ASMBlock> visited = new HashSet<>();
        dfsBlock(entryBlock, ret, visited);
        return ret;
    }

    private void dfsBlock(ASMBlock block, ArrayList<ASMBlock> order, HashSet<ASMBlock> visited) {
        order.add(block);
        visited.add(block);
        for (ASMBlock successor : block.getSuccessors()) {
            if (!visited.contains(successor))
                dfsBlock(successor, order, visited);
        }
    }

    public void accept(ASMVisitor visitor) { visitor.visit(this); }
}
