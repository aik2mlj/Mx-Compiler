package backend;

import riscv.ASMBlock;
import riscv.ASMFunction;
import riscv.ASMModule;
import riscv.ASMVisitor;
import riscv.instuctions.ASMInst;
import riscv.operands.GlobalVar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class BugEmitter implements ASMVisitor {
    private File outputFile;
    private OutputStream os;
    private PrintWriter writer;
    private String indent;

    private int functionCnt;

    public BugEmitter(String filename, ASMModule module) {
        if (filename != null) {
            try {
                outputFile = new File(filename);
                assert outputFile.exists() || outputFile.createNewFile();
                os = new FileOutputStream(filename);
//                System.err.println(os);
                writer = new PrintWriter(os);
            } catch (Exception err) {
                throw new RuntimeException();
            }
        } else {
            os = null;
            writer = null;
        }
        indent = "\t";
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
        if (os != null)
            writer.print(str);
    }

    private void printIdt(String str) {
        print(indent + str);
    }

    private void println(String str) {
        if (os != null)
            writer.println(str);
    }

    private void printlnIdt(String str) {
        println(indent + str);
    }

    @Override
    public void visit(ASMModule module) {
        printlnIdt(".text");
        println("");

        module.getFuncMap().values().forEach(function -> function.accept(this));

        println("");
        println(indent + ".section\t.sdata,\"aw\",@progbits"); // just copy that sh!t
        module.getGlobalVarMap().values().forEach(globalVar -> globalVar.accept(this));
    }

    @Override
    public void visit(ASMFunction function) {
//        if (function.getName().equals("__init__"))
//            return;
        printlnIdt(".globl\t" + function.getName());
        printlnIdt(".p2align\t2");
        printlnIdt(".type\t" + function.getName() + ",@function");

        println(function.getName() + ":");
        function.getBlocks().forEach(asmBlock -> asmBlock.accept(this));
        println("");
    }

    @Override
    public void visit(ASMBlock block) {
        println(block.getName() + ":" + "                                             " + "# " + block.getIrName());

        for (ASMInst asmInst = block.getHeadInst(); asmInst != null; asmInst = asmInst.next) {
            printIdt(asmInst.toString());
            //
//            print(" ".repeat(50 - asmInst.toString().length()) + "#use: ");
//            asmInst.getUses().forEach(use -> print(use.toString() + " "));
//            print(" #def: ");
//            asmInst.getDefs().forEach(def -> print(def.toString()));
            println("");
        }
    }

    @Override
    public void visit(GlobalVar globalVar) {
        if (globalVar.getVarType() != GlobalVar.VarType.STRING) {
            printlnIdt(".globl\t" + globalVar.getName());
            printlnIdt(".p2align\t2");
        }
        println(globalVar.getName() + ":");
        println(globalVar.emit());
        println("");
    }
}

