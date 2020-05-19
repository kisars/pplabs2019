package ru.spbstu.telematics.java;

import ru.spbstu.telematics.java.entity.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Application {

    private static final int ROOMS_COUNT = 10;
    /** period in milliseconds */
    private static final long PERIOD_FAN = 200L;
    private static final long PERIOD_HEATER = 200L;
    private static final long PERIOD_COMPUTER = 200L;
    private static final long PERIOD_ROOM = 200L;
    private static final double INC_TEMPERATURE = 0.5;
    private static final double DEC_TEMPERATURE = 0.5;
    private static final double TEMPERATURE_SETPOINT = 25;
    private static final double OUTSIDE_TEMPERATURE = 30;
    /** deltas in percent */
    private static final double TEMPERATURE_DELTA = 1;
    /** default room params */
    private static final AtomicReference<Double> DEFAULT_TEMPERATURE =  new AtomicReference<>(20.0);

    private static final ForkJoinPool THREAD_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) throws InterruptedException {

        Map<String, Computer> computers = defineComputers();
        computers.forEach((name, computer) -> {
            System.out.println(name + " started");
            computer.start();
            computer.room.start();
        });
        Thread.sleep(30000);
        computers.forEach((name, computer) -> {
            computer.stop();
            computer.room.stop();
        });
        THREAD_POOL.shutdown();
        while (!THREAD_POOL.awaitTermination(5L, TimeUnit.SECONDS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
    }

    private static Map<String, Computer> defineComputers() {
        HashMap<String, Computer> computers = new HashMap<>();
        for (int i = 0; i < ROOMS_COUNT; i++) {
            String computerName = "Computer - " + i;
            Computer computer = new Computer(
                    THREAD_POOL,
                    defineTemperatureSensor(i),
                    defineFan(i),
                    defineHeater(i),
                    defineRoom(i),
                    computerName,
                    TEMPERATURE_SETPOINT,
                    TEMPERATURE_DELTA,
                    PERIOD_COMPUTER
            );
            computers.put(computerName, computer);
        }
        return computers;
    }

    private static Room defineRoom(int i) {
        return new Room(THREAD_POOL, "Room - " + i, DEFAULT_TEMPERATURE, OUTSIDE_TEMPERATURE, PERIOD_ROOM);
    }

    private static Heater defineHeater(int i) {
        return new Heater(THREAD_POOL, "Heater - " + i, INC_TEMPERATURE, PERIOD_HEATER);
    }

    private static Fan defineFan(int i) {
        return new Fan(THREAD_POOL, "Fan - " + i, DEC_TEMPERATURE, PERIOD_FAN);
    }

    private static TemperatureSensor defineTemperatureSensor(int i) {
        return new TemperatureSensor("TemperatureSensor - " + i);
    }
}
