package me.langolierra.customqueue;

public interface ICustomQueue<T> {
	void push(T element);
	T pop();
	T front();
	T back();
	int size();
	boolean empty();
}