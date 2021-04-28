package ir;

import ir.instruction.*;
import ir.operand.Operand;
import ir.operand.Parameter;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;
import ir.type.VoidType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Function {
    private Module module;
    private String name;
    private IRType retType;
    private ArrayList<Parameter> parameters;

    private LinkedList<Block> blocks;
    private Block entryBlock, exitBlock;
    private Block retBlock; // is not compulsory, not linked at first.
    private Register retValue;
    // there is only one exitBlock. If the function has more than one "return",
    // just store the "retval" and load it in the exitBlock.
    private Register thisAddr;

    private HashSet<Register> allocRegs;
    private SymbolTable symbolTable;

    // ADCE
    private boolean sideEffect = false;

    public Function(Module module, String name, IRType retType, ArrayList<Parameter> parameters) {
        this.module = module;
        this.name = name;
        this.retType = retType;
        this.parameters = parameters;

        this.entryBlock = this.exitBlock = this.retBlock = null;
        this.blocks = new LinkedList<>();
        this.symbolTable = new SymbolTable();
        retValue = null;
        thisAddr = null;
        // put parameters.
        for(Parameter parameter: parameters) {
            parameter.setFunction(this);
        }
        allocRegs = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public void setEntryBlock(Block entryBlock) {
        assert blocks.contains(entryBlock);
        this.entryBlock = entryBlock;
    }

    public Block getEntryBlock() {
        return entryBlock;
    }

//    public void setExitBlock(Block exitBlock) {
//        assert blocks.contains(exitBlock);
//        this.exitBlock = exitBlock;
//    }

    public Block getExitBlock() {
        return exitBlock;
    }

    public Block getRetBlock() {
        return retBlock;
    }

    public IRType getRetType() {
        return retType;
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public ArrayList<Block> getOrderBlocks() {
        // this is used to avoid concurrencyException when iterating blocks
        return new ArrayList<>(blocks);
    }

    public ArrayList<Block> getDFSBlocks() {
        ArrayList<Block> dfs = new ArrayList<>();
        HashSet<Block> visited = new HashSet<>();
        dfsBlock(entryBlock, dfs, visited);
        return dfs;
    }

    private void dfsBlock(Block block, ArrayList<Block> dfs, HashSet<Block> visited) {
        dfs.add(block);
        visited.add(block);
        for (Block suc : block.getSuccessors()) {
            if (!visited.contains(suc))
                dfsBlock(suc, dfs, visited);
        }
    }

    public void removeBlock(Block block) {
        block.getSuccessors().forEach(suc -> suc.getPredecessors().remove(block));
        block.getSuccessors().forEach(Block::cleanPhis);
        blocks.remove(block);
        if (retBlock == block)
            retBlock = block.getPredecessors().iterator().next(); // FIXME
        if (blocks.isEmpty())
            entryBlock = exitBlock = null;
    }

    public void appendBlock(Block newBlock) {
        blocks.add(newBlock);
        addSymbol(newBlock);
        if(entryBlock == null)
            entryBlock = newBlock;
        exitBlock = newBlock;
    }

//    public void insertBlockBefore(Block block0, Block newBlock) {
//        assert blocks.contains(block0);
//        blocks.add(blocks.indexOf(block0), newBlock);
//    }

    public void initialize() {
        Block newBlock = new Block(this, "entry");
        this.appendBlock(newBlock);
        retBlock = new Block(this, "return"); // not linked yet.

        if(retType instanceof VoidType)
            retBlock.appendInst(new RetInst(retBlock, new VoidType(), null));
        else {
            retValue = new Register(new PointerType(retType), "retval.addr", this);
            entryBlock.appendInst(new AllocaInst(entryBlock, retValue, retType));
            allocRegs.add(retValue);
            entryBlock.appendInst(new StoreInst(entryBlock, retType.getDefaultValue(), retValue)); // FIXME: is this correct?
            Register loadRetValue = new Register(retType, "retval", this);
            retBlock.appendInst(new LoadInst(retBlock, retValue, loadRetValue));
            retBlock.appendInst(new RetInst(retBlock, retType, loadRetValue));
        }
    }

    public void addSymbol(Operand operand) {
        symbolTable.add(operand);
    }

    public void addSymbol(Block block) {
        symbolTable.addBlock(block);
    }

    public void setRetBlock(Block retBlock) {
        this.retBlock = retBlock;
    }

    public void appendRetBlock() {
        this.appendBlock(retBlock);
    }

    public Register getRetValue() {
        return retValue;
    }

    public void setRetValue(Register retValue) {
        this.retValue = retValue;
    }

    public void setThisAddr(Register thisAddr) {
        this.thisAddr = thisAddr;
    }

    public Register getThisAddr() {
        return thisAddr;
    }

    public Operand getThisParam() { return parameters.get(0); }

    public HashSet<Register> getAllocRegs() {
        return allocRegs;
    }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    public boolean hasSideEffect() {
        return sideEffect;
    }

    public void setSideEffect(boolean sideEffect) {
        this.sideEffect = sideEffect;
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
