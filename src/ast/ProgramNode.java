package ast;

import util.Position;

import java.util.ArrayList;

public class ProgramNode extends ASTNode {
    // (classDef | funcDef | varDef)*
    private ArrayList<ClassDefNode> classDefNodes;
    private ArrayList<FuncDefNode> funcDefNodes;
    private ArrayList<VarNode> globalVars;

    public ProgramNode(Position pos, ArrayList<ClassDefNode> classDefNodes,
                       ArrayList<FuncDefNode> funcDefNodes,
                       ArrayList<VarNode> globalVars) {
        super(pos);
        this.classDefNodes = classDefNodes;
        this.funcDefNodes = funcDefNodes;
        this.globalVars = globalVars;
    }

    public ArrayList<ClassDefNode> getClassDefNodes() {
        return classDefNodes;
    }

    public ArrayList<FuncDefNode> getFuncDefNodes() {
        return funcDefNodes;
    }

    public ArrayList<VarNode> getGlobalVars() {
        return globalVars;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<ProgramNode>\n");
        ret.append("classDefNodes:\n");
        for(ClassDefNode it: classDefNodes) ret.append(it.toString());
        ret.append("funcDefNodes:\n");
        for(FuncDefNode it: funcDefNodes) ret.append(it.toString());
        ret.append("globalVars:\n");
        for(VarNode it: globalVars) ret.append(it.toString());
        return ret.toString();
    }
}
