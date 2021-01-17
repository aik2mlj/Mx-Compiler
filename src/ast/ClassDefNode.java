package ast;

import util.Position;

import java.util.ArrayList;

public class ClassDefNode extends ASTNode {
    private String identifier;
    private ArrayList<VarNode> members;
    private FuncDefNode constructor;
    private ArrayList<FuncDefNode> methods;

    public ClassDefNode(Position pos, String identifier, ArrayList<VarNode> members,
                        FuncDefNode constructor, ArrayList<FuncDefNode> methods) {
        super(pos);
        this.identifier = identifier;
        this.members = members;
        this.constructor = constructor;
        this.methods = methods;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ArrayList<VarNode> getMembers() {
        return members;
    }

    public boolean hasConstructor() { return constructor != null; }

    public FuncDefNode getConstructor() {
        return constructor;
    }

    public ArrayList<FuncDefNode> getMethods() {
        return methods;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<ClassDefNode>\n");
        ret.append("identifier: " + identifier + "\n");
        ret.append("members:\n");
        for(VarNode it: members) ret.append(it.toString());
        if(constructor != null) ret.append("constructor: " + constructor.toString() + '\n');
        ret.append("methods:\n");
        for(FuncDefNode it: methods) ret.append(it.toString());
        return ret.toString();
    }
}
