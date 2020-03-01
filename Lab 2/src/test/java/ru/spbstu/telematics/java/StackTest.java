package ru.spbstu.telematics.java;

import org.junit.Test;
import java.util.Stack;
import org.junit.Assert;

public class StackTest 
{
	MyStack<Integer> mine = new MyStack<Integer>(5);
	Stack<Integer> standart = new Stack<Integer>();
	
	@Test
	public void testpush()
	{
		for (int i=0; i < 10000; i++)
		{
			mine.push(i);
			standart.push(i);
			Assert.assertEquals(mine.peek(),standart.peek());
			Assert.assertEquals(mine.getSize(),standart.size());
		}
	}
	
	@Test
	public void testpop()
	{
		for (int i=0; i < 10000; i++)
		{
			mine.push(i);
			standart.push(i);
		}
		for (int i=0; i < 10000; i++)
		{
			Assert.assertEquals(mine.pop(),standart.pop());
			Assert.assertEquals(mine.getSize(),standart.size());
		}
		
	}
	
	@Test
	public void testEmpty()
	{
		Assert.assertEquals(mine.empty(),standart.empty());
		mine.push(123);
		standart.push(123);
		Assert.assertEquals(mine.empty(),standart.empty());
		mine.pop();
	}
	
	@Test
	public void testiterator()
	{
		int[] arr1= {1,2,3,4,5,6,7};
		for (int i=1; i <= 7; i++)
			mine.push(i);
		int i=0;
		for (int item : mine)
		{
			Assert.assertEquals(item,arr1[i]);
			i++;
		}

	}
	
    
}
