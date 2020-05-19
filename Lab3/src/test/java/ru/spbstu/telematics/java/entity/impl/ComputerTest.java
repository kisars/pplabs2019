package ru.spbstu.telematics.java.entity.impl;


import org.junit.Test;
import org.junit.Assert;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class ComputerTest {

    /** period in milliseconds */
    private static final long FAN_PERIOD = 50L;
    private static final long HEATER_PERIOD = 50L;
    private static final long ROOM_PERIOD = 500L;
    private static final double INC_TEMPERATURE = 0.3;
    private static final double DEC_TEMPERATURE = 0.3;
    private static final double OUTSIDE_TEMPERATURE = -5;
    private static final double DELTA_TEMPERATURE = 0.3;
    		

    private static final ForkJoinPool THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    private Computer computer;    

    @Test
    public void checkProcessingSuccess() throws InterruptedException {

        // GIVEN
    	computer = defineComputer();
        Room room = computer.room;
        room.temperature = new AtomicReference<>(23.0);
        computer.temperatureSetPoint = 20.0;
        computer.temperatureDelta = 1;
        computer.period = 100;

        // WHEN
        computer.start();
        room.start();
        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        computer.stop();
        room.stop();

        // THEN
        Assert.assertEquals(20.0, room.temperature.get(),DELTA_TEMPERATURE);
    }

    private Computer defineComputer() {
        return new Computer(THREAD_POOL, defineTemperatureSensor(), defineFan(), defineHeater(), defineRoom(), "Test computer", 0, 0, 0);
    }

    private Room defineRoom() {
        return new Room(THREAD_POOL, "Test room", new AtomicReference<>(0.0), OUTSIDE_TEMPERATURE, ROOM_PERIOD);
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