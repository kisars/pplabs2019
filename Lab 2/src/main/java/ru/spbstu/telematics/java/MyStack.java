package ru.spbstu.telematics.java;

import java.util.Arrays;
import java.util.Iterator;

public class MyStack<T> implements IStack<T>,Iterable<T> 
{
	int size;
	int lim;
	Object[] objects;
	
	public MyStack(int count)
	{
		objects = new Object[count];
		size = 0;
		lim = count;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public boolean  contains(T item)
	{
		for (int i = 0; i < lim; i++)
		{
			if (objects[i] == item)
				return true;
		}
		return false;
	}
	
	public int search(T item)
	{
		for (int i = 0; i < lim; i++)
		{
			if (objects[i] == item)
				return i;
		}
		return -1;
	}
	
	public boolean empty()
	{
		if (size == 0)
			return true;
		else
			return false;	
	}
	
	public T pop()
	{
		if (!empty())
		{
			size--;
			return (T)objects[size];
		}
			
		else
			return null;
	}
	
	public T peek()
	{
		if (!empty())
			return (T)objects[size-1];			
		else
			return null;
	}
	
	public void push(T item)
	{
		if (lim  == size)
		{
			this.lim += 5;
			Object[] newArr = Arrays.copyOf(objects, lim);
			objects = newArr;
		}
		objects[size] = item;
		size++;
	}
	
	@Override
    public Iterator<T> iterator() 
	{
        return new Iterator<T>() 
        {
            int i = 0;
            
            @Override
            public boolean hasNext() 
            {
                return i < size-1;
            }
            
            @Override
            public T next() 
            {
                T result = (T)objects[i];
                i++;
                return result;
            }            
        };
	}
	
}