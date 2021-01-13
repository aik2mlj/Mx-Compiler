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
        String name = "test.Mx";
        InputStream input = new FileInputStream(name);
        try {
            MxLexer lexer = new MxLexer(CharStreams.fromStream(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());

            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());

            ParseTree parseTreeRoot = parser.program(); // root: program node
        } catch (Error err) {
            System.err.println(err.toString());
            throw new RuntimeException();
        }
    }
}