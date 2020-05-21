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

    private static CompletableFuture<BigDecimal> calcPi(int start, long numElements, int scale) {

        var f = new CompletableFuture<BigDecimal>();

        ForkJoinPool.commonPool().execute((() -> {
            BigDecimal sum = BigDecimal.ZERO;
            for (int i = start; i < start + numElements; i++) {

                var first = new BigDecimal (4.0).divide(new BigDecimal(8 * i + 1), scale, RoundingMode.HALF_UP);
                var second =new BigDecimal (-2.0).divide(new BigDecimal(8 * i + 4), scale, RoundingMode.HALF_UP);
                var third = new BigDecimal (-1.0).divide(new BigDecimal(8 * i + 5), scale, RoundingMode.HALF_UP);
                var forth = new BigDecimal (-1.0).divide(new BigDecimal(8 * i + 6), scale, RoundingMode.HALF_UP);
                var mult = new BigDecimal(1.0).divide(new BigDecimal(16.0).pow(i), scale, RoundingMode.HALF_UP);
                sum = sum.add(mult.multiply(first.add(second).add(third).add(forth)));
            }


            f.complete(sum);
        }));

        return f;
    }


    private static CompletableFuture<BigDecimal> calcE(int start, int numElements, int scale) {

        var f = new CompletableFuture<BigDecimal>();

        ForkJoinPool.commonPool().execute(() -> {
            BigDecimal sum = BigDecimal.ZERO;

            for (int i = start; i < start + numElements; i++) {
                var x = BigDecimal.ONE.divide(calculateFactorial(i), scale, RoundingMode.HALF_UP);
                sum = sum.add(x);
            }
            
            f.complete(sum);
        });

        return f;
    }

    static CompletableFuture<BigDecimal> calculate(int numThreads, int elementsPerThread, boolean e, int scale) {

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
        int totalElements = 50000;
        int numThreads = 8;
        int elementsPerThread = totalElements / numThreads;

        System.out.println("Calculating pi with " + numThreads + " threads and " + elementsPerThread + " elements per thread");

        var startTime = System.currentTimeMillis();
        var pi = calculate(numThreads, elementsPerThread, false, scale).join().setScale(scale, RoundingMode.HALF_UP);
        var elapsed = System.currentTimeMillis() - startTime;
        System.out.println(pi);
        System.out.println(elapsed + " ms\n");

        totalElements = 1000;
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