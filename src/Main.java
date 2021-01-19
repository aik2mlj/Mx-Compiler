import ast.ProgramNode;
import frontend.ASTBuilder;
import frontend.SemanticChecker;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.FileInputStream;
import java.io.InputStream;

import util.error.Error;
import util.MxErrorListener;
import parser.MxLexer;
import parser.MxParser;

public class Main {
    public static void main(String[] args) throws Exception {
        String name = "test.mx";
        InputStream inputStream;
        CharStream input;
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
            ProgramNode astRoot = (ProgramNode)astBuilder.visit(parseTreeRoot);

            SemanticChecker semanticChecker = new SemanticChecker();
            astRoot.accept(semanticChecker);
        } catch (Error err) {
            System.err.println(err.toString());
            throw new RuntimeException();
        }
    }
}