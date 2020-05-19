package ru.spbstu.telematics.java.entity.impl;

public class TemperatureSensor {

    private String name;

    public TemperatureSensor(String name) {
        this.name = name;
    }

    public double getActualValue(Room room) {
        return room.temperature.get();
    }
}
