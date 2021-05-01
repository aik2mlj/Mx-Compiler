package ir;

import ir.instruction.*;
import ir.operand.*;
import ir.type.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

public class IRPrinter implements IRVisitor {
    private File outputFile;
    private OutputStream os;
    private PrintWriter writer;
    private String indent;

    public IRPrinter(String filename, Module module) {
        try {
            outputFile = new File(filename);
            assert outputFile.exists() || outputFile.createNewFile();
            os = new FileOutputStream(filename, false);
            writer = new PrintWriter(os);
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException(err.getMessage());
        }
        indent = "    ";

        module.accept(this);
        try {
            writer.close();
            os.close();
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException(err.getMessage());
        }
    }

    private void print(String str) {
        writer.print(str);
    }

    private void printIdt(String str) {
        writer.print(indent + str);
    }

    private void println(String str) {
        writer.println(str);
    }

    private void printlnIdt(String str) {
        writer.println(indent + str);
    }

    @Override
    public void visit(Module module) {
        module.getStructMap().values().forEach(structType -> {
            print(structType.toString() + " = type { ");
            for (int i = 0; i < structType.getMemberList().size(); ++i) {
                print(structType.getMemberList().get(i).toString());
                if (i != structType.getMemberList().size() - 1)
                    print(", ");
            }
            println(" }");
            println("");
        });
        module.getGlobalVarMap().values().forEach(globalVar -> {
            println(globalVar.toStringWithoutType() + " = global " + ((PointerType) globalVar.getType()).getBaseType()
                    + " " + globalVar.getInitValue().toStringWithoutType());
        });
        println("");
        module.getFuncMap().values().forEach(function -> {
            function.accept(this);
            println("");
        });
        module.getBuiltInFuncMap().values().forEach(function -> {
            print("declare " + function.getRetType().toString() + " @" + function.getName() + "(");
            for (int i = 0; i < function.getParameters().size(); ++i) {
                print(function.getParameters().get(i).getType().toString());
                if (i != function.getParameters().size() - 1)
                    print(", ");
            }
            println(")");
        });
    }

    @Override
    public void visit(Function function) {
        print("define " + function.getRetType().toString() + " @" + function.getName() + "(");
        for (int i = 0; i < function.getParameters().size(); ++i) {
            var param = function.getParameters().get(i);
            print(function.getParameters().get(i).toString());
            if (i != function.getParameters().size() - 1)
                print(", ");
        }
        println(") {");

        for (int i = 0; i < function.getBlocks().size(); ++i) {
            var block = function.getBlocks().get(i);
            block.accept(this);
            if (i != function.getBlocks().size() - 1)
                println("");
        }
        println("}");
    }

    @Override
    public void visit(Block block) {
        print(block.getName() + ":" + (block.getPredecessors().size() > 0 ? "                                    ; preds = " : ""));
        Iterator<Block> it = block.getPredecessors().iterator();
        while(it.hasNext()) {
            print(it.next().toString());
            if (it.hasNext()) print(", ");
        }
        // ----------------
//        print(" ; succs = ");
//        it = block.getSuccessors().iterator();
//        while(it.hasNext()) {
//            print(it.next().toString());
//            if (it.hasNext()) print(", ");
//        }
        // ----------------
        if (block.getParentFunc().getRetBlock() == block)
            print("  ; return block");
        if (block.getParentFunc().getEntryBlock() == block)
            print("  ; entry block");
        println("");
        for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
            printIdt(inst.toString());
            println("");
//            println("\t; live: " + inst.isLive());
//            inst.getUses().forEach(use -> print(use + " "));
//            println("");
        }
    }

    @Override
    public void visit(AllocaInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = alloca " + inst.getType().toString());
    }

    @Override
    public void visit(BinaryInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = " + inst.getOperator().toString() + " " +
                inst.getLhs().toString() + ", " + inst.getRhs().toStringWithoutType());
    }

    @Override
    public void visit(BitcastToInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = bitcast " + inst.getSrc().toString() + " to " +
                inst.getDstType().toString());
    }

    @Override
    public void visit(BrInst inst) {
        printIdt("br " + (inst.getCondition() != null ? inst.getCondition().toString() + ", " : "") +
                "label " + inst.getTrueBlock().toString() + (inst.getFalseBlock() != null ? ", label " + inst.getFalseBlock().toString() : ""));
    }

    @Override
    public void visit(CallInst inst) {
        printIdt((inst.getDstReg() != null? inst.getDstReg().toStringWithoutType() + " = " : "") + "call " +
                inst.getFunction().getRetType().toString() + " @" + inst.getFunction().getName() + "(");
        for (int i = 0; i < inst.getParameters().size(); ++i) {
            print(inst.getParameters().get(i).toString());
            if (i != inst.getParameters().size() - 1)
                print(", ");
        }
        print(")");
    }

    @Override
    public void visit(GetElementPtrInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = getelementptr " +
                ((PointerType) inst.getPointer().getType()).getBaseType().toString() + ", " +
                inst.getPointer().toString() + ", ");
        for (int i = 0; i < inst.getIndices().size(); ++i) {
            print(inst.getIndices().get(i).toString());
            if (i != inst.getIndices().size() - 1)
                print(", ");
        }
    }

    @Override
    public void visit(IcmpInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = icmp " + inst.getOperator().toString() + " " +
                inst.getLhs().toString() + ", " + inst.getRhs().toStringWithoutType());
    }

    @Override
    public void visit(LoadInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = load " + inst.getType().toString() + ", " +
                inst.getPointer().toString());
    }

    @Override
    public void visit(PhiInst inst) {
        printIdt(inst.getDstReg().toStringWithoutType() + " = phi " + inst.getDstReg().getType().toString() + " ");
        for (int i = 0; i < inst.getPredecessors().size(); ++i) {
            print("[ " + inst.getValues().get(i).toStringWithoutType() + ", " + inst.getPredecessors().get(i).toString() + " ]");
            if (i != inst.getPredecessors().size() - 1)
                print(", ");
        }
    }

    @Override
    public void visit(RetInst inst) {
        printIdt("ret " + (inst.getRetValue() != null ? inst.getRetValue().toString() : inst.getRetType().toString()));
    }

    @Override
    public void visit(StoreInst inst) {
        printIdt("store " + ((PointerType) inst.getPointer().getType()).getBaseType().toString() + " " +
                inst.getValue().toStringWithoutType() + ", " + inst.getPointer().toString());
    }

    @Override
    public void visit(MoveInst inst) {
        printIdt("move " + inst.getDstReg().toString() + ", " + inst.getSrc().toString());
    }
}
