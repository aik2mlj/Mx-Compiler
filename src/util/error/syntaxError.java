package util.error;
import util.Position;

public class syntaxError extends Error {
    public syntaxError(String msg, Position pos) {
        // "super" relates to its father
        super("SyntaxError: " + msg, pos);
    }
}
