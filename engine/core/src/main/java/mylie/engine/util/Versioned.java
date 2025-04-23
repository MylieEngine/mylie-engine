package mylie.engine.util;

import lombok.Getter;

public class Versioned<T> {
	@Getter
	private T value;
	private long version;
	@Getter
	private long frameId;

	public Versioned(T value) {
		this.value = value;
	}

	public void value(T value, long frameId) {
		this.value = value;
		this.frameId = frameId;
		this.version++;
	}

	public Ref<T> ref() {
		return new Ref<>(this);
	}

	public static class Ref<T> {
		final Versioned<T> versioned;
		@Getter
		private T value;
		private long version;
		@Getter
		private long frameId;

		private Ref(Versioned<T> versioned) {
			this.versioned = versioned;
			this.value = versioned.value;
			this.version = versioned.version;
			this.frameId = versioned.frameId;
		}

		private Ref(Versioned<T> versioned, T value, long version, long frameId) {
			this.versioned = versioned;
			this.value = value;
			this.version = version;
			this.frameId = frameId;
		}

		public boolean isUpToDate() {
			return versioned.version <= version;
		}

		public boolean update() {
			if (versioned.version > version) {
				value = versioned.value;
				version = versioned.version;
				frameId = versioned.frameId;
				return true;
			}
			return false;
		}

		public T value(boolean update) {
			if (update && !isUpToDate()) {
				update();
			}
			return value();
		}

		public long frameId(boolean update) {
			if (update && !isUpToDate()) {
				update();
			}
			return frameId();
		}

		public Ref<T> ref() {
			return new Ref<>(versioned, value, version, frameId);
		}
	}
}
