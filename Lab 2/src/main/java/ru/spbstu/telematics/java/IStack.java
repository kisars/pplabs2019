package ru.spbstu.telematics.java;

public interface IStack<T> {
	int size=0; 
	int lim=0;
	boolean contains(T item);
	void push(T item);
	T pop(); 
	T peek();
	int getSize();
	int search(T item);
	boolean empty();
}
