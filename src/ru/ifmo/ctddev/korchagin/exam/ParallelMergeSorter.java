package ru.ifmo.ctddev.korchagin.exam;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelMergeSorter implements Sorter {
	

	final int threadCount;
/**
 * Creates a new ParallelMergeSorter set to use specified number of threads
 * @param threads
 */
	public ParallelMergeSorter(int threads) {
		threadCount = threads;
	}

	@Override
	public <T extends Comparable<T>> void sort(T[] data) {
		sort(data, Comparable<T>::compareTo);
	}
	
	class MergeWorker<T> implements Runnable{
		
		private final Comparator<T> comp;
		private final T[] data, result;
		private final AtomicInteger ai;
		private final int chunkSize;
		
		MergeWorker(Comparator<T> comp, T[] data, T[] result, AtomicInteger ai, int chunkSize){
			this.comp = comp;
			this.data = data;
			this.result = result;
			this.ai = ai;
			this.chunkSize = chunkSize;
		}

		void merge(int startPos, int nextPos, int endPos) {
			int resPos = startPos;
			int firstPos = startPos;
			int secondPos = nextPos;
			while (firstPos < nextPos && secondPos < endPos) {
				if (comp.compare(data[firstPos], data[secondPos]) <= 0) {
					result[resPos] = data[firstPos];
					resPos++;
					firstPos++;
				} else {
					result[resPos] = data[secondPos];
					resPos++;
					secondPos++;
				}
			}
			while(firstPos < nextPos){
				result[resPos] = data[firstPos];
				resPos++;
				firstPos++;
			}
			while (secondPos < endPos){
				result[resPos] = data[secondPos];
				resPos++;
				secondPos++;				
			}
			for (int i = startPos; i < endPos; i++) {
				data[i] = result[i];
			}
		}

		@Override
		public void run() {
			while(true){
				int currentJob = ai.getAndIncrement();
				int firstPos = currentJob * chunkSize;
				if(currentJob * chunkSize + chunkSize / 2 >= data.length){
					return;
				}
				merge(firstPos, firstPos + chunkSize / 2, Math.min(firstPos + chunkSize, data.length));
			}
		}
	}
	
	@Override
	public <T> void sort(T[] data, Comparator<T> comp) {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(data.getClass().getComponentType(), data.length);
		AtomicInteger ai = new AtomicInteger();
		List<Future<?>> list = new ArrayList<>();
		for(int sortedSize = 1; sortedSize < data.length; sortedSize *= 2){
			for(int i = 0; i < threadCount; i++){
				new MergeWorker<T>(comp, data, result, ai, sortedSize * 2).run();
				list.add(executor.submit(new MergeWorker<T>(comp, data, result, ai, sortedSize * 2)));
			}
			for(Future<?> f : list){
				try {
					f.get();
				} catch (InterruptedException | ExecutionException e) {
					return;
				}
			}
			list.clear();
			ai.set(0);
		}
		executor.shutdown();
	}

}
