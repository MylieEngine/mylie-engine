package mylie.engine.core.async;

import java.util.concurrent.*;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

public final class SchedulingStrategies {
	private SchedulingStrategies() {
		throw new IllegalInstantiationException(SchedulingStrategies.class);
	}

	public static class SingleThread extends SchedulingStrategy {
		private final DirectExecutor directExecutor = new DirectExecutor();

		public SingleThread() {
			super(false);
		}

		@Override
		TaskExecutor executor(Target target, Consumer<Runnable> drain) {
			if (target.managed()) {
				return directExecutor;
			} else {
				return new SubmitExecutor(target, drain);
			}
		}

		@Override
		public boolean multiThread() {
			return false;
		}

		@Override
		protected WorkerThread createWorkerThread(Target target) {
			return new NoOpWorkerThread(target);
		}
	}

	abstract static class MultiThread extends SchedulingStrategy {
		MultiThread() {
			super(true);
		}

		@Override
		public boolean multiThread() {
			return true;
		}

		@Override
		protected WorkerThread createWorkerThread(Target target) {
			return new ThreadBasedWorkerThread(target);
		}

		static class ExecutorExecutor implements TaskExecutor {
			private final ExecutorService executor;

			public ExecutorExecutor(ExecutorService executor) {
				this.executor = executor;
			}

			@Override
			public <R> void execute(Result<R> result) {
				CompletableFuture.supplyAsync(result::execute, executor);
			}
		}
	}

	public static class MultiThreadExecutor extends MultiThread {
		private final ExecutorService executorService;

		public MultiThreadExecutor(ExecutorService executorService) {
			this.executorService = executorService;
		}

		@Override
		TaskExecutor executor(Target target, Consumer<Runnable> drain) {
			if (target == Target.BACKGROUND) {
				return new ExecutorExecutor(executorService);
			} else {
				return new SubmitExecutor(target, drain);
			}
		}
	}

	static class SubmitExecutor implements SchedulingStrategy.TaskExecutor {
		@Getter
		private final Target target;
		private final Consumer<Runnable> drain;

		public SubmitExecutor(Target target, Consumer<Runnable> drain) {
			this.target = target;
			this.drain = drain;
		}

		@Override
		public <R> void execute(Result<R> result) {
			drain.accept(() -> Async.executeTask(result));
		}
	}

	static class DirectExecutor implements SchedulingStrategy.TaskExecutor {
		@Override
		public <R> void execute(Result<R> result) {
			Async.executeTask(result);
		}
	}

	static class NoOpWorkerThread extends WorkerThread implements Consumer<Runnable> {
		NoOpWorkerThread(Target target) {
			super(target);
		}

		@Override
		public void accept(Runnable runnable) {
			runnable.run();
		}

		@Override
		protected Consumer<Runnable> drain() {
			return this;
		}

		@Override
		public void stop() {

		}
	}

	@Slf4j
	static class ThreadBasedWorkerThread extends WorkerThread implements Runnable {
		private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
		private final Thread thread;
		private boolean running = false;
		public ThreadBasedWorkerThread(Target target) {
			super(target);
			this.thread = new Thread(this, target.name());
			this.thread.start();
		}

		@Override
		protected Consumer<Runnable> drain() {
			return queue::add;
		}

		@Override
		public void run() {
			if (log.isTraceEnabled()) {
				log.trace("Thread< {} > started", thread.getName());
			}
			running = true;
			target().bind();
			while (running) {
				try {
					Runnable poll = queue.poll(100, TimeUnit.MILLISECONDS);
					if (poll != null) {
						poll.run();
					}
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			target().release();
			if (log.isTraceEnabled()) {
				log.trace("Thread< {} > stopped", thread.getName());
			}
		}

		@Override
		public void stop() {
			CountDownLatch latch = new CountDownLatch(1);
			queue.add(() -> {
				running = false;
				latch.countDown();
			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
