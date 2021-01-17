package ast;

import util.Position;

abstract public class StmtNode extends ASTNode {
    public StmtNode(Position pos) {
        super(pos);
    }
}
