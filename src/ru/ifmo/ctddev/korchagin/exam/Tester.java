package ru.ifmo.ctddev.korchagin.exam;

import java.util.Comparator;
import java.util.Random;

public class Tester {

	static final int TEST_ARRAY_SIZE = 100;
	/**
	 * Checks whether provided array is sorted using given comparator
	 * @param data array to check for being sorted
	 * @param cmp {@link Comparator} to be used for checking
	 * @return True is array is sorted, False otherwise
	 */
	public static <T> boolean isArraySorted(T[] data, Comparator<T> cmp) {
		int n = data.length;
		for (int i = 1; i < n; ++i) {
			if (cmp.compare(data[i - 1], data[i]) > 0) {
				return false;
			}
		}
		return true;
	}

	Random rand = new Random();

	/**
	 * Checks whether provided {@link Sorter} sorts an array using natural ordering
	 * @param s {@link Sorter} to be checked
	 */
	public void testNaturalOrdering(Sorter s){
		Integer[] target = new Integer[TEST_ARRAY_SIZE];
		for(int i = 0; i < target.length; i++){
			target[i] = rand.nextInt();
		}
		s.sort(target);
		assert(isArraySorted(target, Integer::compare));
	}
	/**
	 * Checks whether provided {@link Sorter} sorts an array using given {@link Comparator}
	 * @param s {@link Sorter} to be checked
	 */
	public void testComparator(Sorter s){
		Integer[] target = new Integer[TEST_ARRAY_SIZE];
		for(int i = 0; i < target.length; i++){
			target[i] = rand.nextInt();
		}
		Comparator<Integer> cp1 = Integer::compare;
		cp1 = cp1.reversed();
		Comparator<Integer> cp2 = (t, u) -> (Integer.valueOf(t % 1000).compareTo(Integer.valueOf(u % 1000)));
		s.sort(target, cp1);
		assert(isArraySorted(target, cp1));
		s.sort(target, cp2);
		assert(isArraySorted(target, cp2));
	}

	/**
	 * Runs tests for ParallelMergeSorter set to use 1 and 10 threads
	 */
	public void test(){
		Sorter s = new ParallelMergeSorter(1);
		testNaturalOrdering(s);
		testComparator(s);
		s = new ParallelMergeSorter(10);
		testNaturalOrdering(s);
		testComparator(s);
	}
	
	/**
	 * Runs tests for ParallelMergeSorter set to use 1 and 10 threads
	 */
	public static void main(String[] args) {
		new Tester().test();
	}

}
