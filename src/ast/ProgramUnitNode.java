package ast;

import util.Position;

abstract public class ProgramUnitNode extends ASTNode {
    // ClassDef / FuncDef / VarNode
    public ProgramUnitNode(Position pos) {
        super(pos);
    }
}
