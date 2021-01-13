package util;

//import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Position {
    private int row, column;

    public Position(int r, int c) {
        row = r; column = c;
    }
    public Position(Token token) {
        row = token.getLine();
        column = token.getCharPositionInLine();
    }
    public Position(TerminalNode terminal) {
        this(terminal.getSymbol());
    }

    public int row() { return row; }
    public int column() { return column; }
    public String toString() { return row + "," + column; }
}
