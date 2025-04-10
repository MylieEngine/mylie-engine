package mylie.engine.core;

public interface ShutdownReason {
	record Normal(String reason) implements ShutdownReason {
	}
	record Error(Throwable throwable) implements ShutdownReason {
	}
	record Restart(EngineSettings engineSettings) implements ShutdownReason {
	}
}
