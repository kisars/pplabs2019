package ru.spbstu.telematics.java;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;

import java.math.BigDecimal;
public class CalcConstantsTest
    extends TestCase
{

    public CalcConstantsTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( CalcConstantsTest.class );
    }

    public void testPi()
    {
        BigDecimal testPi = new BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068");

        int scale = 100;
        int totalElements = 50000;
        int numWorkers = 16;
        int elementsPerWorker = totalElements / numWorkers;
        
        var pi = CalcConstants.calculate(numWorkers, elementsPerWorker, false, scale).join();
        var difference = testPi.subtract(pi);
        System.out.println("PI: " + pi);
        System.out.println("Difference:" + difference.abs());
        Assert.assertEquals( difference.abs().compareTo(new BigDecimal(Math.pow(10, -1 * scale ))),-1);
    }

    public void testE()
    {
        BigDecimal testE = new BigDecimal("2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427");
        int scale = 100;
        int totalElements = 1500;
        int numWorkers = 8;
        int elementsPerWorker = totalElements / numWorkers;
        var e = CalcConstants.calculate(numWorkers, elementsPerWorker, true, scale).join();
        var difference = testE.subtract(e);
        System.out.println("E: " + e);
        System.out.println("Difference:" + difference.abs());
        Assert.assertEquals( difference.abs().compareTo(new BigDecimal(Math.pow(10, -1 * (scale-1) ))),-1);
    }
}