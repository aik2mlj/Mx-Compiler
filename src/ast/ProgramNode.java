package ast;

import util.Position;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
    // (classDef | funcDef | varDef)*
    private ArrayList<ProgramUnitNode> programUnitNodes;

    public ProgramNode(Position pos, ArrayList<ProgramUnitNode> programUnitNodes) {
        super(pos);
        this.programUnitNodes = programUnitNodes;
    }

    public ArrayList<ProgramUnitNode> getProgramUnitNodes() {
        return programUnitNodes;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<ProgramNode>\n");
        ret.append("programUnitNode:\n");
        for(var it: programUnitNodes) ret.append(it.toString());
        return ret.toString();
    }
}
