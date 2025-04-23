package mylie.engine.core.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
public final class Result<T> {
	private final Hash hash;
	private final long version;
	private final Target target;
	private final Supplier<T> supplier;
	private final CompletableFuture<T> future;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private Result(Target target, Hash hash, long version, Supplier<T> supplier, CompletableFuture<T> future) {
		this.hash = hash;
		this.version = version;
		this.target = target;
		this.supplier = supplier;
		this.future = future;
	}

	T execute() {
		return Async.executeTask(this);
	}

	public T get() {
		if (!future.isDone() && (target == Target.BACKGROUND || target.current())) {
			execute();
		}
		return future().join();
	}

	public CompletionStage<Void> thenAccept(Consumer<T> consumer) {
		return future.thenAccept(consumer);
	}

	static <T> Result<T> of(Target target, Hash hash, long version, Supplier<T> supplier) {
		return new Result<>(target, hash, version, supplier, new CompletableFuture<>());
	}

	public static <T> Result<T> of(T value) {
		CompletableFuture<T> future = new CompletableFuture<>();
		future.complete(value);
		return new Result<>(null, null, 0, null, future);
	}
}
