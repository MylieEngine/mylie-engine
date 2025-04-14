package mylie.engine.core.async;

record Hash(Functions.Base function, int value, Object... args) {
	Hash(Functions.Base function, Object... args) {
		this(function, calculateHash(function, args), args);
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public String toString() {
		return "Hash: " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Hash hash) {
			return hash.value == value;
		}
		return false;
	}

	private static int calculateHash(Functions.Base function, Object... args) {
		int tmp = function.hashCode();
		for (Object arg : args) {
			if (arg instanceof CustomHash custom) {
				tmp = tmp * 31 + custom.customHash();
			} else {
				tmp = tmp * 31 + arg.hashCode();
			}
		}
		return tmp;
	}
}
