package util.error;

import util.Position;

public class IRBuildingError extends Error {
    public IRBuildingError(String msg, Position position) {
        super("IRBuildingError: " + msg, position);
    }
}
