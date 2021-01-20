package util;

import ast.SingleTypeNode;
import ast.TypeNode;
import ast.VoidTypeNode;
import util.entity.Entity;
import util.entity.FuncEntity;
import util.entity.VarEntity;
import util.error.SemanticError;
import util.type.Type;
import util.type.TypeTable;

import java.util.ArrayList;
import java.util.HashMap;

public class Scope {
    public enum ScopeType {
        ProgramScope, ClassScope, FunctionScope, BlockScope, LoopScope
    }
    private Scope parentScope;

    private ScopeType scopeType;
    private TypeNode funcReturnTypeNode; // if this is a FunctionScope: used for return
    private Type classType; // if this is a ClassScope: used for "this"
    private HashMap<String, Entity> entities;

    public Scope(Scope parentScope, ScopeType scopeType, TypeNode funcReturnTypeNode, Type classType) {
         this.parentScope = parentScope;
         this.scopeType = scopeType;
         this.funcReturnTypeNode = funcReturnTypeNode;
         this.classType = classType;
         entities = new HashMap<>();
    }

    public Scope getParentScope() {
        return parentScope;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public TypeNode getFuncReturnTypeNode() {
        return funcReturnTypeNode;
    }

    public Type getClassType() {
        return classType;
    }

    public void addBuiltInFunction() {
        Position pos = new Position(0, 0);
        ArrayList<VarEntity> params;
        FuncEntity function;
        // void print(string str);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("string", "str"));
        function = new FuncEntity("print", pos, new VoidTypeNode(pos), params, null,
                FuncEntity.EntityType.Function);
        entities.put("print", function);

        // void println(string str);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("string", "str"));
        function = new FuncEntity("println", pos, new VoidTypeNode(pos), params, null,
                FuncEntity.EntityType.Function);
        entities.put("println", function);

        // void printInt(int n);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("int", "n"));
        function = new FuncEntity("printInt", pos, new VoidTypeNode(pos), params, null,
                FuncEntity.EntityType.Function);
        entities.put("printInt", function);

        // void printlnInt(int n);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("int", "n"));
        function = new FuncEntity("printlnInt", pos, new VoidTypeNode(pos), params, null,
                FuncEntity.EntityType.Function);
        entities.put("printlnInt", function);

        // string getString();
        params = new ArrayList<>();
        function = new FuncEntity("getString", pos, new SingleTypeNode(pos, "string"), params, null,
                FuncEntity.EntityType.Function);
        entities.put("getString", function);

        // int getInt();
        params = new ArrayList<>();
        function = new FuncEntity("getInt", pos, new SingleTypeNode(pos, "int"), params, null,
                FuncEntity.EntityType.Function);
        entities.put("getInt", function);

        // string toString(int i);
        params = new ArrayList<>();
        params.add(VarEntity.newBuiltInParam("int", "i"));
        function = new FuncEntity("toString", pos, new SingleTypeNode(pos, "string"), params, null,
                FuncEntity.EntityType.Function);
        entities.put("toString", function);
    }

    public void DefineEntity(Entity entity, TypeTable typeTable) {
        if(entities.containsKey(entity.getName())) {
            if(entity instanceof VarEntity)
                throw new SemanticError("Duplicate declaration of variable \"" + entity.getName() + "\"", entity.getPos());
            else if(entity instanceof FuncEntity)
                throw new SemanticError("Duplicate declaration of function \"" + entity.getName() + "\"", entity.getPos());
        } else {
            if(typeTable.hasType(new SingleTypeNode(null, entity.getName()))) {
                if (entity instanceof VarEntity)
                    throw new SemanticError("Variable \"" + entity.getName() + "\" has same identifier with a type", entity.getPos());
                else if (entity instanceof FuncEntity) {
                    if(((FuncEntity) entity).getEntityType() != FuncEntity.EntityType.Constructor) // not constructor
                        throw new SemanticError("Function \"" + entity.getName() + "\" has same identifier with a type", entity.getPos());
                }
            } else
                entities.put(entity.getName(), entity);
        }
    }

//    public Entity getEntity(String name) {
//        // search in entities. If not found, search in parentScope.
//        if(entities.containsKey(name))
//            return entities.get(name);
//        else if(parentScope != null)
//            return parentScope.getEntity(name);
//        else
//            return null;
//    }

    public VarEntity getVarEntity(String name) {
        // search in funcEntities
        if(entities.containsKey(name) && entities.get(name) instanceof VarEntity)
            return (VarEntity) entities.get(name);
        else if(parentScope != null)
            return parentScope.getVarEntity(name);
        else
            return null;
    }

    public FuncEntity getFuncEntity(String name) {
        // search in funcEntities
        if(entities.containsKey(name) && entities.get(name) instanceof FuncEntity)
            return (FuncEntity) entities.get(name);
        else if(parentScope != null)
            return parentScope.getFuncEntity(name);
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
