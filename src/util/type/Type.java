package util.type;

abstract public class Type {
    // name: ID | int | bool | string; members: if this is a class, class members.
    private String typeName;

    public Type(String typeName) {
        this.typeName = typeName;
    }

    public boolean isTerminalType() {
        return typeName.equals("int") || typeName.equals("bool") || typeName.equals("string") || typeName.equals("void");
    }

    public String getTypeName() {
        return typeName;
    }
}
