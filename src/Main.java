import ast.ProgramNode;
import backend.*;
import frontend.ASTBuilder;
import frontend.SemanticChecker;
import ir.*;
import ir.Module;
import optimize.LoopTreeConstructor;
import optimize.Optimization;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;

import riscv.ASMModule;
import util.error.Error;
import util.MxErrorListener;
import parser.MxLexer;
import parser.MxParser;

public class Main {
    public static void main(String[] args) throws Exception {
        boolean opti = true;
        String name = "test.mx";
        InputStream inputStream;
        CharStream input;
        boolean just_sema = args.length > 0 && args[0].equals("--sema");
        try {
            inputStream = new FileInputStream(name);
            input = CharStreams.fromStream(inputStream);
        } catch (Exception e) {
            System.err.println("Cannot open test.Mx");
            throw new RuntimeException();
        }
        try {
            MxLexer lexer = new MxLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());
            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            ParseTree parseTreeRoot = parser.program(); // root: program node

            ASTBuilder astBuilder = new ASTBuilder();
            ProgramNode astRoot = (ProgramNode) astBuilder.visit(parseTreeRoot);

            SemanticChecker semanticChecker = new SemanticChecker();
            astRoot.accept(semanticChecker);

            if (!just_sema) {
                IRBuilder irBuilder = new IRBuilder(semanticChecker.getGlobalScope(), semanticChecker.getTypeTable());
                astRoot.accept(irBuilder);
                Module module = irBuilder.getModule();

                // SSA && Optimize

//                new IRPrinter("IRcout0.ll", module);
                new CFGSimplifier(module, false).run();
                var dominancer = new Dominancer(module);
                dominancer.run();
//                dominancer.print();
                new ResolveAlloca(module).run();
//                new IRPrinter("IRcout.ll", module);


                if (opti) new Optimization(module).run();
//                new IRPrinter("Optcout.ll", module);

                new ResolvePhi(module).run();
//                new CFGSimplifier(module, true).run();

//                new IRPrinter("SSAcout.ll", module);

                InstSelector instSelector = new InstSelector();
                module.accept(instSelector);
                ASMModule asmModule = instSelector.getAsmModule();
                // ---------
//                new BugEmitter("bug.s", asmModule);
                // ---------
                new Dominancer(module).run();
                module.getFuncMap().values().forEach(function -> new LoopTreeConstructor(function, false).runFunc());
                new RegisterAllocator(asmModule).run();
                new StackOverFlow(asmModule);
//                new BugEmitter("bug.s", asmModule);
                new CodeEmitter("output.s", asmModule, false);
            }
        } catch (Error err) {
            System.err.println(err.toString());
            throw new RuntimeException();
        }
    }
}