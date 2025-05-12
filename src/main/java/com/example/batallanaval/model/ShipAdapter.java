package com.example.batallanaval.model;

public class ShipAdapter implements IShip {
    private final String name;
    private final int size;

    public ShipAdapter(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return size;
    }
}
