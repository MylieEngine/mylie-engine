package mylie.engine.core.async;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Async {

	public static <R, P0> Result<R> async(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache,
			long version, Functions.One<P0, R> function, P0 p0) {
		Hash hash = new Hash(function, p0);
		return execute(scheduler, mode, target, cache, version, hash, () -> function.execute(p0));
	}

	public static <R> Result<R> async(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache, long version,
			Functions.Zero<R> function) {
		Hash hash = new Hash(function);
		return execute(scheduler, mode, target, cache, version, hash, function::execute);
	}

	private static <R> Result<R> execute(Scheduler scheduler, ExecutionMode mode, Target target, Cache cache,
			long version, Hash hash, Supplier<R> supplier) {
		Result<R> result;
		try (Cache.Lock _ = cache.getLock(hash)) {
			result = cache.result(hash, version);
			if (result != null) {
				return result;
			}
			result = Result.of(target, hash, version, supplier);
			cache.result(hash, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
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
