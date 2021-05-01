package util.type;

import ast.ArrayTypeNode;
import ast.SingleTypeNode;
import ast.TypeNode;
import ast.VoidTypeNode;
import util.Position;
import util.error.SemanticError;

import java.util.LinkedHashMap;

public class TypeTable {
    private LinkedHashMap<TypeNode, Type> typeTable;

    public TypeTable() {
        typeTable = new LinkedHashMap<>();

        Position pos = new Position(0, 0);
        typeTable.put(new SingleTypeNode(pos, "int"), new IntType());
        typeTable.put(new SingleTypeNode(pos, "bool"), new BoolType());
        typeTable.put(new SingleTypeNode(pos, "string"), new StringType());
        typeTable.put(new VoidTypeNode(pos), new VoidType());
    }

    public LinkedHashMap<TypeNode, Type> getTypeTable() {
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
        if(typeNode instanceof ArrayTypeNode) {
            var baseType = this.getType(((ArrayTypeNode) typeNode).getBaseTypeNode());
            return new ArrayType(baseType, ((ArrayTypeNode) typeNode).getDimension());
        }
        else if(hasType(typeNode))
            return typeTable.get(typeNode);
        else throw new SemanticError("No type \"" + typeNode.getTypeName() + "\" found", typeNode.getPos());
    }
}
