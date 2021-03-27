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

    private Map<String, Integer> blockMap;
    private Map<String, Integer> symbolMap;

    public IRPrinter(String filename, Module module) {
        blockMap = new HashMap<>();
        symbolMap = new HashMap<>();
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
            structType.accept(this);
            println("");
        });
        module.getGlobalVarMap().values().forEach(globalVar -> {
            globalVar.accept(this);
        });
        module.getConstStringMap().values().forEach(constString -> constString.accept(this));
        println("");
        module.getFuncMap().values().forEach(function -> {
            function.accept(this);
            println("");
        });
    }

    @Override
    public void visit(IRFunction function) {
        print("define " + function.getRetType().toString() + " @" + function.getName() + "(");
        for (int i = 0; i < function.getParameters().size(); ++i) {
            print(function.getParameters().get(i).toString());
            if (i != function.getParameters().size() - 1)
                print(", ");
        }
        println(") {");
        for (int i = 0; i < function.getBlocks().size(); ++i) {
            function.getBlocks().get(i).accept(this);
            if (i != function.getBlocks().size() - 1)
                println("");
        }
        println("}");
    }

    @Override
    public void visit(IRBlock block) {
        print(block.getName() + ":" + (block.getPrecursors().size() > 0 ? "                                    ; preds = " : ""));
        Iterator<IRBlock> it = block.getPrecursors().iterator();
        while(it.hasNext()) {
            print(it.next().toString());
            if (it.hasNext()) print(", ");
        }
        println("");
        block.getInsts().forEach(irInst -> {
            irInst.accept(this);
        });
    }

    @Override
    public void visit(AllocaInst inst) {
        printlnIdt(inst.getDstReg().toString() + " = alloca " + inst.getType().toString());
    }

    @Override
    public void visit(BinaryInst inst) {
        printlnIdt(inst.getDstReg().toString() + " = " + inst.getOperator().toString() + " " +
                inst.getLhs().toString() + " " + inst.getRhs().toString());
    }

    @Override
    public void visit(BitcastToInst inst) {
        printlnIdt(inst.getDstReg().toString() + " = bitcast " + inst.getSrc().toString() + " to " +
                inst.getDstType().toString());
    }

    @Override
    public void visit(BrInst inst) {
        printlnIdt("br " + (inst.getCondition() != null ? inst.getCondition().toString() + ", " : "") +
                "label " + inst.getTrueBlock().toString() + (inst.getFalseBlock() != null ? ", label " + inst.getFalseBlock().toString() : ""));
    }

    @Override
    public void visit(CallInst inst) {
        printIdt((inst.getDstReg() != null? inst.getDstReg().toString() + " = " : "") + "call " +
                inst.getFunction().getRetType().toString() + " @" + inst.getFunction().getName() + "(");
        for (int i = 0; i < inst.getParameters().size(); ++i) {
            print(inst.getParameters().get(i).toString());
            if (i != inst.getParameters().size() - 1)
                print(", ");
        }
        println(")");
    }

    @Override
    public void visit(GetElementPtrInst inst) {
        printIdt(inst.getDstReg().toString() + " = getelementptr " +
                ((PointerType) inst.getPointer().getType()).getBaseType().toString() + ", " +
                inst.getPointer().toString() + ", ");
        for (int i = 0; i < inst.getIndices().size(); ++i) {
            print(inst.getIndices().get(i).toString());
            if (i != inst.getIndices().size() - 1)
                print(", ");
        }
        println("");
    }

    @Override
    public void visit(IcmpInst inst) {
        printlnIdt(inst.getDstReg().toString() + " = icmp " + inst.getOperator().toString() + " " +
                inst.getLhs().toString() + ", " + inst.getRhs().toString());
    }

    @Override
    public void visit(LoadInst inst) {
        printlnIdt(inst.getDstReg().toString() + " = load " + inst.getType().toString() + ", " +
                inst.getPointer().toString());
    }

    @Override
    public void visit(PhiInst inst) {
        printIdt(inst.getDstReg().toString() + " = phi ");
        for (int i = 0; i < inst.getPredecessors().size(); ++i) {
            print("[ " + inst.getValues().get(i).toString() + ", " + inst.getPredecessors().get(i).toString() + " ]");
            if (i != inst.getPredecessors().size() - 1)
                print(", ");
        }
        println("");
    }

    @Override
    public void visit(RetInst inst) {
        printlnIdt("ret " + (inst.getRetValue() != null ? inst.getRetValue().toString() : inst.getRetType().toString()));
    }

    @Override
    public void visit(StoreInst inst) {
        printlnIdt("store " + inst.getValue().toString() + ", " + inst.getPointer().toString());
    }

    @Override
    public void visit(ConstInt operand) {

    }

    @Override
    public void visit(ConstBool operand) {

    }

    @Override
    public void visit(ConstNull operand) {

    }

    @Override
    public void visit(ConstString operand) {

    }

    @Override
    public void visit(GlobalVar operand) {
        println(operand.toString() + " = global " + operand.getInitValue().toString());
    }

    @Override
    public void visit(Parameter operand) {
    }

    @Override
    public void visit(Register operand) {
    }

    @Override
    public void visit(ArrayType type) {

    }

    @Override
    public void visit(IntType type) {

    }

    @Override
    public void visit(PointerType type) {

    }

    @Override
    public void visit(StructType type) {
        print(type.toString() + " = type { ");
        for (int i = 0; i < type.getMemberList().size(); ++i) {
            print(type.getMemberList().get(i).toString());
            if (i != type.getMemberList().size() - 1)
                print(", ");
        }
        println(" }");
    }

    @Override
    public void visit(VoidType type) {

    }
}
