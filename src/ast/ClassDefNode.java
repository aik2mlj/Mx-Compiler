package ast;

import util.Position;
import util.entity.FuncEntity;
import util.entity.VarEntity;
import util.type.ClassType;

import java.util.ArrayList;

public class ClassDefNode extends ProgramUnitNode {
    private String identifier;
    private ArrayList<VarNode> members;
    private FuncDefNode constructor;
    private ArrayList<FuncDefNode> methods;

    private ClassType classType;

    public ClassDefNode(Position pos, String identifier, ArrayList<VarNode> members,
                        FuncDefNode constructor, ArrayList<FuncDefNode> methods) {
        super(pos);
        this.identifier = identifier;
        this.members = members;
        this.constructor = constructor;
        this.methods = methods;
        this.classType = null;
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

    public ClassType getClassType() {
        if(classType != null) return classType;
        ArrayList<VarEntity> entityMembers = new ArrayList<>();
        FuncEntity entityConstructor = null;
        ArrayList<FuncEntity> entityMethods = new ArrayList<>();
        for(var it: members)
            entityMembers.add(it.getEntity(VarEntity.EntityType.Member));
        if(constructor != null)
            entityConstructor = constructor.getEntity(FuncEntity.EntityType.Constructor);
        for(var it: methods)
            entityMethods.add(it.getEntity(FuncEntity.EntityType.Method));
        this.classType = new ClassType(identifier, entityMembers, entityConstructor, entityMethods);
        return classType;
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
