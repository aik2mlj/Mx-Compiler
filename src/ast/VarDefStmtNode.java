package ast;

import util.Position;

import java.util.ArrayList;

public class VarDefStmtNode extends StmtNode {
    private ArrayList<VarNode> varNodes;

    public VarDefStmtNode(Position pos, ArrayList<VarNode> varNodes) {
        super(pos);
        this.varNodes = varNodes;
    }

    public ArrayList<VarNode> getVarNodes() {
        return varNodes;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<VarDefStmtNode>\nvarNodes\n");
        for(VarNode it: varNodes) ret.append(it.toString());
        return ret.toString();
    }
}
