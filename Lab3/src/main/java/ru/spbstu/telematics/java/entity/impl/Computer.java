package ru.spbstu.telematics.java.entity.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;

public class Computer {

    public final Executor threadPool;
    public final TemperatureSensor temperatureSensor;
    public final Fan fan;
    public final Heater heater;
    public final Room room;

    public String name;
    public double temperatureSetPoint;
    
    /** temperature tolerance in percent */
    public double temperatureDelta = 1;

    /** Check limits period in milliseconds */
    public long period = 1000L;

    private CompletableFuture<Void> future;

    public Computer(Executor threadPool, TemperatureSensor temperatureSensor, Fan fan, Heater heater, Room room, String name, double temperatureSetPoint, double temperatureDelta, long period) {
        this.threadPool = threadPool;
        this.temperatureSensor = temperatureSensor;
        this.fan = fan;
        this.heater = heater;
        this.room = room;
        this.name = name;
        this.temperatureSetPoint = temperatureSetPoint;
        this.temperatureDelta = temperatureDelta;
        this.period = period;
    }

    public boolean start() {
        this.future = runAsync(
                () -> {
                	System.out.println("Current room temperature is:" + this.room.temperature);
                    if (isHighTemperature()) {
                        System.out.println(name + " Fan is on, heater is off.");
                        fanOn();
                        heaterOff();
                    } else if (isLowTemperature()) {
                        System.out.println(name + " Fan is off, heater is on.");
                        fanOff();
                        heaterOn();
                    } else if (!isLowTemperature()) {
                    	System.out.println(name + " Fan is off, heater is off.");
                        fanOff();
                        heaterOff();
                    }
                    start();
                },
                delayedExecutor(period, TimeUnit.MILLISECONDS, threadPool)
        );
        return true;
    }

    public boolean stop() {
        if (this.future == null) {
            return false;
        } else {
            this.future.cancel(true);
            return true;
        }
    }

    public boolean isHighTemperature() {
        return temperatureSensor.getActualValue(room) > temperatureSetPoint+ temperatureDelta / 100 * temperatureSetPoint  ;
    }

    public boolean isLowTemperature() {
        return temperatureSensor.getActualValue(room) < temperatureSetPoint - temperatureDelta / 100 * temperatureSetPoint ;
    }

    public boolean fanOn() {
        return fan.start(room);
    }

    public boolean fanOff() {
        return fan.stop(room);
    }

    public boolean heaterOn() {
        return heater.start(room);
    }

    public boolean heaterOff() {
        return heater.stop(room);
    }
}
