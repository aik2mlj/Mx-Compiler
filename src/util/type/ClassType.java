package util.type;

import util.entity.FuncEntity;
import util.entity.VarEntity;

import java.util.ArrayList;

public class ClassType extends Type {
    private ArrayList<VarEntity> members;
    private FuncEntity constructor;
    private ArrayList<FuncEntity> methods;

    public ClassType(String typeName, ArrayList<VarEntity> members, FuncEntity constructor, ArrayList<FuncEntity> methods) {
        super(typeName);
        this.members = members;
        this.constructor = constructor;
        this.methods = methods;
    }

    public ArrayList<VarEntity> getMembers() {
        return members;
    }

    public boolean hasConstructor() {
        return constructor != null;
    }

    public FuncEntity getConstructor() {
        return constructor;
    }

    public ArrayList<FuncEntity> getMethods() {
        return methods;
    }

    public VarEntity getMember(String name) {
        for(VarEntity it: members) {
            if (it.getName().equals(name))
                return it;
        }
        return null;
    }

    public FuncEntity getMethod(String name) {
        for(FuncEntity it: methods) {
            if (it.getName().equals(name))
                return it;
        }
        return null;
    }
}
