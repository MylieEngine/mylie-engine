package mylie.engine.core.async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Cache {
	public static final Cache NO = new NoOpCache();
	public static final Cache ONE_FRAME = new OneFrameCache(new ConcurrentHashMap<>());

	public static void registerDefaults(Scheduler scheduler) {
		scheduler.register(NO);
		scheduler.register(ONE_FRAME);
	}

    abstract <R> Result<R> result(Hash hash, long version);

	abstract <R> void result(Hash hash, Result<R> result);

	abstract void onUpdate();

	abstract void clear();

	abstract Lock getLock(Hash hash);

	private abstract static class MapCache extends Cache {
		final java.util.Map<Hash, Result<?>> results;
		public MapCache(Map<Hash, Result<?>> results) {
			this.results = results;
		}
	}

	private static class OneFrameCache extends MapCache {
		private final ReentrantLock lock = new ReentrantLock();
		public OneFrameCache(Map<Hash, Result<?>> results) {
			super(results);
		}

		@SuppressWarnings("unchecked")
		@Override
		<R> Result<R> result(Hash hash, long version) {
			Result<?> result = results.get(hash);
			return result == null ? null : (Result<R>) result;
		}

		@Override
		<R> void result(Hash hash, Result<R> result) {
			results.put(hash, result);
		}

		@Override
		void onUpdate() {
			results.clear();
		}

		@Override
		void clear() {
			results.clear();
		}

		@Override
		Lock getLock(Hash hash) {
			return lock;
		}
	}

	private static class NoOpCache extends Cache {
		@Override
		<R> Result<R> result(Hash hash, long version) {
			// Return null intentional
			return null;
		}

		@Override
		<R> void result(Hash hash, Result<R> result) {
			// Do nothing here
		}

		@Override
		void onUpdate() {
			// Do nothing here
		}

		@Override
		void clear() {
			// Do nothing here
		}

		@Override
		Lock getLock(Hash hash) {
			return null;
		}
	}
}
