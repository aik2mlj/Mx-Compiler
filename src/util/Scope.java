package util;

import util.entity.Entity;
import util.entity.FuncEntity;
import util.entity.VarEntity;
import util.error.SemanticError;

import java.util.HashMap;

public class Scope {
    public enum ScopeType {
        ProgramScope, ClassScope, FunctionScope, BlockScope, LoopScope
    }
    private Scope parentScope;

    private ScopeType scopeType;
    private HashMap<String, Entity> entities;

    public Scope getParentScope() {
        return parentScope;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public void DefineEntity(Entity entity, Position pos) {
        if(entities.containsKey(entity.getName())) {
            if(entity instanceof VarEntity)
                throw new SemanticError("Duplicate declaration of variable \"" + entity.toString() + "\"", pos);
            else if(entity instanceof FuncEntity)
                throw new SemanticError("Duplicate declaration of function \"" + entity.toString() + "\"", pos);
        } else {
            entities.put(entity.getName(), entity);
        }
    }

    public Entity getEntity(String name) {
        // search in entities. If not found, search in parentScope.
        if(entities.containsKey(name))
            return entities.get(name);
        else if(parentScope != null)
            return parentScope.getEntity(name);
        else
            return null;
    }

    public boolean inClassScope() {
        if (scopeType == ScopeType.ClassScope)
            return true;
        else if (scopeType == ScopeType.ProgramScope)
            return false;
        else
            return parentScope.inClassScope();
    }

    public boolean inFunctionScope() {
        if (scopeType == ScopeType.FunctionScope)
            return true;
        else if (scopeType == ScopeType.ProgramScope)
            return false;
        else
            return parentScope.inFunctionScope();
    }

    public boolean inLoopScope() {
        if (scopeType == ScopeType.LoopScope)
            return true;
        else if (scopeType == ScopeType.ProgramScope)
            return false;
        else
            return parentScope.inLoopScope();
    }

    public boolean inMethodScope() {
        return inClassScope() && inFunctionScope();
    }
}
