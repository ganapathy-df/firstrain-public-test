package com.firstrain.frapi;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FRCompletionService<T> {
	private int submissions;

	public int getSubmissions() {
		return submissions;
	}

	private final CompletionService<T> completionService;

	public Future<T> poll() {
		if (submissions == 0)
			return null;
		submissions--;

		return completionService.poll();
	}

	public Future<T> poll(long timeout, TimeUnit unit) throws InterruptedException {
		if (submissions == 0)
			return null;
		submissions--;

		return completionService.poll(timeout, unit);
	}

	public Future<T> submit(Callable<T> task) {
		submissions++;

		return completionService.submit(task);
	}

	public Future<T> submit(Runnable task, T result) {
		submissions++;

		return completionService.submit(task, result);
	}

	public Future<T> take() throws InterruptedException {
		if (submissions == 0)
			return null;
		submissions--;

		return completionService.take();
	}

	public FRCompletionService(final ExecutorService executorService) {
		this.completionService = new ExecutorCompletionService<T>(executorService);
	}

}
