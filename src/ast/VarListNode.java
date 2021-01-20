package ast;

import util.Position;

import java.util.ArrayList;

public class VarListNode extends ProgramUnitNode {
    // appear in paramList, consists of VarNodes
    private ArrayList<VarNode> varNodes;

    public VarListNode(Position pos, ArrayList<VarNode> varNodes) {
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
        StringBuilder ret = new StringBuilder("<VarListNode>\nvarNodes:\n");
        for(VarNode it: varNodes) ret.append(it.toString());
        return ret.toString();
    }
}
