package ru.spbstu.telematics.java;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.allOf;

public class CalcConstants {
    
    private static BigDecimal calculateFactorial (long number) {
        BigDecimal factorial = BigDecimal.ONE;
        for (int i = 1; i <= number; i++) {
            factorial = factorial.multiply(new BigDecimal(i));
        }
        return factorial;
    }

    private static CompletableFuture<BigDecimal> calcPi(long start, long numElements, int scale) {

        var f = new CompletableFuture<BigDecimal>();

        ForkJoinPool.commonPool().execute((() -> {
            BigDecimal sum = BigDecimal.ZERO;
            for (long i = start; i < start + numElements; i++) {
                sum = sum.add((new BigDecimal(4.0)).divide(new BigDecimal(i).multiply(new BigDecimal(2)).add(BigDecimal.ONE), scale, RoundingMode.HALF_UP));
            }


            f.complete(sum);
        }));

        return f;
    }

    private static CompletableFuture<BigDecimal> calcE(long start, long numElements, int scale) {

        var f = new CompletableFuture<BigDecimal>();

        ForkJoinPool.commonPool().execute(() -> {
            BigDecimal sum = BigDecimal.ZERO;

            for (long i = start; i < start + numElements; i++) {
                var x = BigDecimal.ONE.divide(calculateFactorial(i), scale, RoundingMode.HALF_UP);
                sum = sum.add(x);
            }

            f.complete(sum);
        });

        return f;
    }

    static CompletableFuture<BigDecimal> calculate(long numThreads, long elementsPerThread, boolean e, int scale) {

        List<CompletableFuture<BigDecimal>> futures = list();

        for (int i = 0; i < numThreads; ++i) {
            CompletableFuture<BigDecimal> f;
            if (e)
                f = calcE(i * elementsPerThread, elementsPerThread, scale);
            else
                f = calcPi(i * elementsPerThread, elementsPerThread, scale);
            futures.add(f);
        }

        return allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                .thenApply(CalcConstants::sum);
    }

    public static void main(String[] args) {
        int scale = 100;
        long totalElements = 100;
        long numThreads = 32;
        long elementsPerThread = totalElements / numThreads;

        System.out.println("Calculating pi with " + numThreads + " threads and " + elementsPerThread + " elements per thread");

        var startTime = System.currentTimeMillis();
        var pi = calculate(numThreads, elementsPerThread, false, scale).join().setScale(scale, RoundingMode.HALF_UP);
        var elapsed = System.currentTimeMillis() - startTime;

        System.out.println(pi);
        System.out.println(elapsed + " ms\n");

        totalElements = 1000;
        numThreads = 16;
        elementsPerThread = totalElements / numThreads;
        System.out.println("Calculating e with " + numThreads + " threads and " + elementsPerThread + " elements per threads");

        startTime = System.currentTimeMillis();
        var e = calculate(numThreads, elementsPerThread, true, scale).join().setScale(scale, RoundingMode.HALF_UP);;
        elapsed = System.currentTimeMillis() - startTime;

        System.out.println(e);
        System.out.println(elapsed + " ms");
    }

    private static BigDecimal sum(List<BigDecimal> terms) {
        var result = BigDecimal.ZERO;
        for (BigDecimal t : terms)
            result = result.add(t);
        return result;
    }

    @SafeVarargs
	private static <T> List<T> list(T... entries) {
        return Arrays.stream(entries).collect(Collectors.toList());
    }
}