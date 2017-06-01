package ru.ifmo.ctddev.korchagin.exam;

import java.util.Comparator;

public interface Sorter {
	/**
	 * Sorts provided array using natural ordering
	 * @param data array to be sorted
	 */
	public <T extends Comparable<T>> void sort(T[] data);

	/**
	 * Sorts provided array using given comparator
	 * @param data array to be sorted
	 * @param comp {@link Comparator} to be used for sorting
	 */
	public <T> void sort(T[] data, Comparator<T> comp);
}
