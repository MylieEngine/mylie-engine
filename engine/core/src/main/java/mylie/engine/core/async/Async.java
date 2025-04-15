package mylie.engine.core.async;

import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

@Slf4j
public final class Async {
	private Async() {
		throw new IllegalInstantiationException(Async.class);
	}
	public static <R> Result<R> async(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache, long version,
			Functions.Zero<R> function) {
		Hash hash = new Hash(function);
		return execute(scheduler, mode, target, cache, version, hash, function::execute);
	}

	public static <R, P0> Result<R> async(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache,
			long version, Functions.One<P0, R> function, P0 p0) {
		Hash hash = new Hash(function, p0);
		return execute(scheduler, mode, target, cache, version, hash, () -> function.execute(p0));
	}

	public static <R, P0, P1> Result<R> async(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache,
			long version, Functions.Two<P0, P1, R> function, P0 p0, P1 p1) {
		Hash hash = new Hash(function, p0, p1);
		return execute(scheduler, mode, target, cache, version, hash, () -> function.execute(p0, p1));
	}

	public static <R, P0, P1, P2> Result<R> async(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache,
			long version, Functions.Three<P0, P1, P2, R> function, P0 p0, P1 p1, P2 p2) {
		Hash hash = new Hash(function, p0, p1, p2);
		return execute(scheduler, mode, target, cache, version, hash, () -> function.execute(p0, p1, p2));
	}

	private static <R> Result<R> execute(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache,
			long version, Hash hash, Supplier<R> supplier) {
		Result<R> result;
		Lock lock = cache.getLock(hash);
		if (lock != null) {
			lock.lock();
		}
		try {
			result = cache.result(hash, version);
			if (result != null) {
				return result;
			}
			result = Result.of(target, hash, version, supplier);
			cache.result(hash, result);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
		Async.execute(scheduler, mode, target, result);
		return result;
	}

	private static <R> void execute(Scheduler scheduler, ExecutionMode mode, Target target, Result<R> result) {
		if (executeDirectly(mode, target)) {
			executeTask(result);
		} else {
			scheduler.executeTask(target, result);
		}
	}

	private static boolean executeDirectly(ExecutionMode mode, Target target) {
		if (mode == ExecutionMode.ASYNC) {
			return target.current();
		} else {
			return Target.BACKGROUND == target || target.current();
		}
	}

	static <R> R executeTask(Result<R> result) {
		if (result.running().compareAndSet(false, true)) {
			try {
				R r = result.supplier().get();
				result.future().complete(r);
				return r;
			} catch (Exception e) {
				result.future().completeExceptionally(e);
				log.warn("Exception thrown while executing async task", e);
			}
		}
		return null;
	}
}
