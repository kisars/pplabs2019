package ru.spbstu.telematics.java.entity.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;

public class Heater {

    private final Executor threadPool;

    private String name;

    /** temperature increment */
    private double incTemperature;

    /** temperature delta period in milliseconds */
    private long period;

    private CompletableFuture<Void> future;

    public Heater(Executor threadPool, String name, double incTemperature, long period) {
        this.threadPool = threadPool;
        this.name = name;
        this.incTemperature = incTemperature;
        this.period = period;
    }

    public boolean start(Room room) {
        future = runAsync(
                () -> {
                    room.temperature = new AtomicReference<>(room.temperature.get() + incTemperature);
                    start(room);
                },
                delayedExecutor(period, TimeUnit.MILLISECONDS, threadPool)
        );
        return true;
    }

    public boolean stop() {
        if (future == null) {
            return false;
        } else {
            future.cancel(true);
            return true;
        }
    }
}
