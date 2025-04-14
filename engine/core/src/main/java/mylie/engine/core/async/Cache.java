package mylie.engine.core.async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Cache {
	public static final Cache NO = new NoOpCache();
	public static final Cache ONE_FRAME = new OneFrameCache(new ConcurrentHashMap<>());
	abstract <R> Result<R> result(Hash hash, long version);

	abstract <R> void result(Hash hash, Result<R> result);

	abstract void onUpdate();

	abstract void clear();

	abstract Lock getLock(Hash hash);

	public interface Lock extends AutoCloseable {

	}

	private static abstract class MapCache extends Cache {
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
			System.out.println(hash + " " + result);
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
			lock.lock();
			return lock::unlock;
		}
	}

	private static class NoOpCache extends Cache {
		private static final NoOpLock NO_OP_LOCK = new NoOpLock();
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
			return NO_OP_LOCK;
		}

		private static class NoOpLock implements Lock {
			@Override
			public void close() {

			}
		}
	}
}
