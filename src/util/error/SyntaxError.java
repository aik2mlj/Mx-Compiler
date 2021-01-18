package util.error;
import util.Position;

public class SyntaxError extends Error {
    public SyntaxError(String msg, Position pos) {
        // "super" relates to its father
        super("SyntaxError: " + msg, pos);
    }
}
