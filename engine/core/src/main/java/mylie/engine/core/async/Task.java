package mylie.engine.core.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
@Getter
public abstract class Task<R> {
	private final List<Task<?>> dependencies = new CopyOnWriteArrayList<>();

	public Result<R> execute() {
		List<Result<?>> results = new ArrayList<>();
		dependencies.forEach(task -> results.add(task.execute()));
		results.forEach(Result::get);
		return executeTask();
	}

	protected abstract Result<R> executeTask();
}
