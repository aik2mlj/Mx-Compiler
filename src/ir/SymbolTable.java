package ir;

import ir.operand.Operand;
import ir.operand.Parameter;
import ir.operand.Register;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class SymbolTable {
    private Function function;
    private Map<String, ArrayList<Operand>> symbolTable;
    private Map<String, ArrayList<Block>> blockMap;
//    private LinkedHashMap<String, Operand> regsAndParams;

    private Map<Operand, Register> clonedRegsAndParamsMap;
    private Map<Block, Block> clonedBlockMap;

    public SymbolTable(Function function) {
        this.function = function;
        symbolTable = new LinkedHashMap<>();
        blockMap = new LinkedHashMap<>();
//        regsAndParams = new LinkedHashMap<>();
        clonedBlockMap = new LinkedHashMap<>();
        clonedRegsAndParamsMap = new LinkedHashMap<>();
    }

    public void add(Operand operand) {
        if (operand instanceof Register || operand instanceof Parameter) {
            String bareName = operand.getName();
            String regex = "\\.[0-9]+$";
//            if (bareName.matches("\\.[0-9]+$"))
            bareName = bareName.replaceAll(regex, "");
            if (symbolTable.containsKey(bareName)) {
                var table = symbolTable.get(bareName);
                if (operand instanceof Register) {
                    String newName = bareName + "." + table.size();
//                    if (!operand.getName().endsWith(".addr")) // those "not alloca" registers.
//                        regsAndParams.put(newName, operand);
                    ((Register) operand).rename(newName);
                } else {
                    ((Parameter) operand).rename(bareName + ".." + table.size());
//                    regsAndParams.put(operand.getName(), operand);
                }
                table.add(operand);
            } else {
                ArrayList<Operand> table = new ArrayList<>();
                if (operand instanceof Register)
                    ((Register) operand).rename(bareName);
                else ((Parameter) operand).rename(bareName);
                table.add(operand);
                symbolTable.put(bareName, table);
            }
        }
    }

    public void addBlock(Block block) {
        String bareName = block.getName();
        String regex = "\\.[0-9]+$";
//            if (bareName.matches("\\.[0-9]+$"))
        bareName = bareName.replaceAll(regex, "");
        if (blockMap.containsKey(bareName)) {
            var blocks = blockMap.get(bareName);
            block.rename(bareName + "." + blocks.size());
            blocks.add(block);
        } else {
            ArrayList<Block> blocks = new ArrayList<>();
            block.rename(bareName);
            blocks.add(block);
            blockMap.put(bareName, blocks);
        }
    }

//    public void cloneRegsAndParamsFrom(SymbolTable other) {
//        // return the cloned registers from params.
//        other.regsAndParams.values().forEach(regOrParam -> {
//            if (regOrParam instanceof Register)
//                ((Register) regOrParam).cloneReg(function); // added in "new Register()"
//            else {
//                assert regOrParam instanceof Parameter;
//                ((Parameter) regOrParam).cloneIntoReg(function);
//            }
//        });
//    }

    public Operand getClonedOperand(Operand operand) { // get new operand when cloning
        // cloned operands are already added.
//        if (operand instanceof Register) {
//            if (!regsAndParams.containsKey(operand.getName())) throw new RuntimeException();
//            return regsAndParams.get(operand.getName());
//        }
//        else if (operand instanceof Parameter) // cannot be param: params are cloned into Registers when being inlined.
//            throw new RuntimeException();
//        else return operand;

        if (operand instanceof Register || operand instanceof Parameter) {
            if (clonedRegsAndParamsMap.containsKey(operand)) return clonedRegsAndParamsMap.get(operand);
            Register clonedReg;
            if (operand instanceof Register)
                clonedReg = ((Register) operand).cloneReg(function);
            else clonedReg = ((Parameter) operand).cloneIntoReg(function);
            clonedRegsAndParamsMap.put(operand, clonedReg);
            return clonedReg;
        } else return operand;
    }

    public Block getClonedBlock(Block block) {
        if (block == null) return null;
        if (clonedBlockMap.containsKey(block))
            return clonedBlockMap.get(block);
        else {
            Block clonedBlock = new Block(function, "inline." + block.getParentFunc().getName() + "." + block.getName());
            clonedBlockMap.put(block, clonedBlock);
            // not added to function yet.
            // also no need to call this::addBlock here. Will be added in Function::appendBlock()
            return clonedBlock;
        }
    }

    public void clearCloneCache() {
        clonedBlockMap.clear();
        clonedRegsAndParamsMap.clear();
    }
}
