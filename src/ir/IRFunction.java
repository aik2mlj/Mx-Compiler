package ir;

import ir.instruction.*;
import ir.operand.IROperand;
import ir.operand.Parameter;
import ir.operand.Register;
import ir.type.IRType;
import ir.type.PointerType;
import ir.type.VoidType;

import java.util.ArrayList;
import java.util.LinkedList;

public class IRFunction {
    private Module module;
    private String name;
    private IRType retType;
    private ArrayList<Parameter> parameters;

    private LinkedList<IRBlock> blocks;
    private IRBlock entryBlock, exitBlock;
    private IRBlock retBlock; // is not compulsory, not linked at first.
    private Register retValue;
    // there is only one exitBlock. If the function has more than one "return",
    // just store the "retval" and load it in the exitBlock.
    private Register thisAddr;

    public IRFunction(Module module, String name, IRType retType, ArrayList<Parameter> parameters) {
        this.module = module;
        this.name = name;
        this.retType = retType;
        this.parameters = parameters;

        this.entryBlock = this.exitBlock = this.retBlock = null;
        this.blocks = new LinkedList<>();
        retValue = null;
        thisAddr = null;
        // put parameters.
        for(Parameter parameter: parameters) {
            parameter.setFunction(this);
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public void setEntryBlock(IRBlock entryBlock) {
        assert blocks.contains(entryBlock);
        this.entryBlock = entryBlock;
    }

    public IRBlock getEntryBlock() {
        return entryBlock;
    }

    public void setExitBlock(IRBlock exitBlock) {
        assert blocks.contains(exitBlock);
        this.exitBlock = exitBlock;
    }

    public IRBlock getExitBlock() {
        return exitBlock;
    }

    public IRBlock getRetBlock() {
        return retBlock;
    }

    public IRType getRetType() {
        return retType;
    }

    public LinkedList<IRBlock> getBlocks() {
        return blocks;
    }

    public void appendBlock(IRBlock newBlock) {
        blocks.add(newBlock);
        if(entryBlock == null)
            entryBlock = newBlock;
        exitBlock = newBlock;
    }

    public void insertBlockBefore(IRBlock block0, IRBlock newBlock) {
        assert blocks.contains(block0);
        blocks.add(blocks.indexOf(block0), newBlock);
    }

    public void initialize() {
        IRBlock newBlock = new IRBlock(this, "entry");
        this.appendBlock(newBlock);
        retBlock = new IRBlock(this, "return"); // not linked yet.

        if(retType instanceof VoidType)
            retBlock.appendInst(new RetInst(retBlock, new VoidType(), null));
        else {
            retValue = new Register(new PointerType(retType), "retval.addr");
            entryBlock.appendInst(new AllocaInst(entryBlock, retValue, retType));
            entryBlock.appendInst(new StoreInst(entryBlock, retType.getDefaultValue(), retValue)); // FIXME: is this correct?
            Register loadRetValue = new Register(retType, "retval");
            retBlock.appendInst(new LoadInst(retBlock, retValue, loadRetValue));
            retBlock.appendInst(new RetInst(retBlock, retType, loadRetValue));
        }
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

    public IROperand getThisParam() { return parameters.get(0); }

    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }
}
