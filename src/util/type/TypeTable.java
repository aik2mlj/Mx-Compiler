package util.type;

import ast.SingleTypeNode;
import ast.TypeNode;
import ast.VoidTypeNode;
import util.Position;
import util.error.SemanticError;

import java.util.HashMap;

public class TypeTable {
    private HashMap<TypeNode, Type> typeTable;

    public TypeTable() {
        typeTable = new HashMap<>();

        Position pos = new Position(0, 0);
        typeTable.put(new SingleTypeNode(pos, "int"), new IntType());
        typeTable.put(new SingleTypeNode(pos, "bool"), new BoolType());
        typeTable.put(new SingleTypeNode(pos, "string"), new StringType());
        typeTable.put(new VoidTypeNode(pos), new VoidType());
    }

    public HashMap<TypeNode, Type> getTypeTable() {
        return typeTable;
    }

    public boolean hasType(TypeNode typeNode) {
        return typeTable.containsKey(typeNode);
    }

    public void addType(TypeNode typeNode, Type type) {
        if(hasType(typeNode))
            throw new SemanticError("Duplicate definition of type \"" + typeNode.getTypeName() + "\"", typeNode.getPos());
        else typeTable.put(typeNode, type);
    }

    public Type getType(TypeNode typeNode) {
        if(hasType(typeNode))
            return typeTable.get(typeNode);
        else return null;
    }
}
