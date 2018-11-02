package com.github.Dorae132.easyutil.easyexcel.common;

/**
 * 
 * @author Dorae
 *
 * @param <S> first
 * @param <T> second
 */
public class Pair<S, T> {

	private S first;
	private T second;

	private Pair(S first, T second) {
		this.first = first;
		this.second = second;
	}

	public static <S, T> Pair<S, T> of(S first, T second) {
		return new Pair<S, T>(first, second);
	}

	public S getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}
}

