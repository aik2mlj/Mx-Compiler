package ir;

import ir.operand.Operand;
import ir.operand.Parameter;
import ir.operand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SymbolTable{
    private Map<String, ArrayList<Operand>> symbolTable;
    private Map<String, ArrayList<Block>> blockMap;
    public HashSet<Register> registers;

    public SymbolTable() {
        symbolTable = new HashMap<>();
        blockMap = new HashMap<>();
        registers = new HashSet<>();
    }

    public void add(Operand operand) {
        if (operand instanceof Register || operand instanceof Parameter) {
            if (symbolTable.containsKey(operand.getName())) {
                var table = symbolTable.get(operand.getName());
                if (operand instanceof Register) {
                    ((Register) operand).rename(operand.getName() + "." + table.size());
                    if (!operand.getName().endsWith(".addr")) // those "not alloca" registers.
                        registers.add((Register) operand);
                }
                else ((Parameter) operand).rename(operand.getName() + ".." + table.size());
                table.add(operand);
            } else {
                ArrayList<Operand> table = new ArrayList<>();
                table.add(operand);
                symbolTable.put(operand.getName(), table);
            }
        }
    }

    public void addBlock(Block block) {
        if (blockMap.containsKey(block.getName())) {
            var blocks = blockMap.get(block.getName());
            block.rename(block.getName() + "." + blocks.size());
            blocks.add(block);
        } else {
            ArrayList<Block> blocks = new ArrayList<>();
            blocks.add(block);
            blockMap.put(block.getName(), blocks);
        }
    }
}
