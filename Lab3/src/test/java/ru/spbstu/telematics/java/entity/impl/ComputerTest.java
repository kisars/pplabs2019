package ru.spbstu.telematics.java.entity.impl;


import org.junit.Test;
import org.junit.Assert;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class ComputerTest {

    /** period in milliseconds */
	private long FAN_PERIOD = 50L;
    private long HEATER_PERIOD = 50L;
    private static final long ROOM_PERIOD = 300L;
    private static final long PERIOD_COMPUTER = 100L;
    private static final double INC_TEMPERATURE = 0.3;
    private static final double DEC_TEMPERATURE = 0.3;
    private static final double OUTSIDE_TEMPERATURE = -5;
    private static final double DELTA_TEMPERATURE = 1;
    private static final double TEMPERATURE_SETPOINT = 20;
    private static final double ROOM_TEMPERATURE = 10;
    		

    private static final ForkJoinPool THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    private Computer computer;    
    @Test
    public void checkRaceCondition() throws InterruptedException {
    	FAN_PERIOD = 3000L;
    	HEATER_PERIOD = 3000L;
    	Room room = defineRoom();
    	Fan[] fan = new Fan[100];
    	Heater[] heater = new Heater[100];
    	room.temperature = new AtomicReference<>(10.0);
    	for (int i = 0; i < 100; i++)
    	{
    		fan[i] = defineFan();
        	heater[i] = defineHeater();
    	}
    	for (int i = 0; i < 100; i++)
    	{
    		fan[i].start(room);
        	heater[i].start(room);
    	}
    	Thread.sleep(2000);
    	for (int i = 0; i < 100; i++)
    	{
    		fan[i].stop(room);
        	heater[i].stop(room);
    	}
    	Assert.assertEquals(10.0, room.temperature.get(),0);
    	
    }

    
    @Test
    public void checkProcessingSuccess() throws InterruptedException {
    	FAN_PERIOD = 50L;
    	HEATER_PERIOD = 50L;
        // GIVEN
    	computer = defineComputer();
//        room = defineRoom();
//        room.temperature = new AtomicReference<>(10.0);

        // WHEN
        computer.start();
        computer.room.start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        computer.room.stop();
        computer.stop();       

        // THEN
        Assert.assertEquals(20.0, computer.room.temperature.get(),DELTA_TEMPERATURE);
    }
    

    private Computer defineComputer() {
        return new Computer(THREAD_POOL, defineTemperatureSensor(), defineFan(), defineHeater(), defineRoom(), "Test computer",TEMPERATURE_SETPOINT, DELTA_TEMPERATURE, 0);
    }

    private Room defineRoom() {
        return new Room(THREAD_POOL, "Test room", new AtomicReference<>(ROOM_TEMPERATURE), OUTSIDE_TEMPERATURE, ROOM_PERIOD);
    }

    private Heater defineHeater() {
        return new Heater(THREAD_POOL, "Test heater", INC_TEMPERATURE, HEATER_PERIOD);
    }

    private Fan defineFan() {
        return new Fan(THREAD_POOL, "Test fan", DEC_TEMPERATURE, FAN_PERIOD);
    }

    private TemperatureSensor defineTemperatureSensor() {
        return new TemperatureSensor("Test temperature sensor");
    }
    
    
}