package ru.spbstu.telematics.java.entity.impl;

import static java.util.concurrent.CompletableFuture.delayedExecutor;
import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Room {

	private final Executor threadPool;
	
	private final String name;

    public AtomicReference<Double> temperature;
    
    private CompletableFuture<Void> future;
    
    public final Double outside;
    
    private long period;
    
    public Room(Executor threadPool, String name, AtomicReference<Double> temperature, Double outside, long period) {
    	this.threadPool = threadPool;
    	this.name = name;
        this.temperature = temperature;
        this.outside = outside;
        this.period = period;
    }
    
public boolean start() {
    	
    	if (this.future != null){
		return false;
	}
    	weather();
        return true;
    }
    
    private boolean weather() {
        this.future = runAsync(
                () -> {
                    this.temperature = new AtomicReference<>(this.temperature.get() +  (this.outside - this.temperature.get()) / 100 );
                    weather();
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
            future = null;
            return true;
        }
    }
}
