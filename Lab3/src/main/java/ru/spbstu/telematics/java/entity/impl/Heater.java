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

    private CompletableFuture<Void> future = null;

    public Heater(Executor threadPool, String name, double incTemperature, long period) {
        this.threadPool = threadPool;
        this.name = name;
        this.incTemperature = incTemperature;
        this.period = period;
    }

    public boolean start(Room room) {
    	
    	if (this.future != null){
		return false;
	}
    	heat(room);
        return true;
    }
    
    private boolean heat(Room room) {
        this.future = runAsync(
                () -> {
                    room.temperature = new AtomicReference<>(room.temperature.get() + incTemperature);
                    heat(room);
                },
                delayedExecutor(period, TimeUnit.MILLISECONDS, threadPool)
        );
        return true;
    }

    public boolean stop(Room room) {        
        if (this.future == null) {
            return false;
        } else {
            this.future.cancel(true);
            this.future = null;
            return true;
        }
    }
}
