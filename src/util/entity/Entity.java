package util.entity;

import util.Position;

abstract public class Entity {
    // variable/function
    private String name;
    private Position pos;

    public Entity(String name, Position pos) {
        this.pos = pos;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Position getPos() {
        return pos;
    }
}
