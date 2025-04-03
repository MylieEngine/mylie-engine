package mylie.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.Getter;


@Getter
public class Build {
	private static final Info INFO = new Info();

	public static String engineVersion() {
		return INFO.engineVersion();
	}

	public static String lastTag() {
		return INFO.lastTag();
	}

	public static String commitDistance() {
		return INFO.commitDistance();
	}

	public static String gitHash() {
		return INFO.gitHash();
	}

	public static String gitHashFull() {
		return INFO.gitHashFull();
	}

	public static String branchName() {
		return INFO.branchName();
	}

	public static String isCleanTag() {
		return INFO.isCleanTag();
	}

	public static String buildTime() {
		return INFO.buildTime();
	}

	@Getter
	private static class Info {
		final String engineVersion;
		private final String lastTag;
		private final String commitDistance;
		private final String gitHash;
		private final String gitHashFull;
		private final String branchName;
		private final String isCleanTag;
		private final String buildTime;

		private Info() {
			Properties properties = new Properties();
			try (InputStream versionPropertiesStream = Info.class
					.getResourceAsStream("/mylie/engine/version.properties")) {
				if (versionPropertiesStream == null) {
					throw new IllegalStateException("Version properties file does not exist");
				}
				properties.load(new InputStreamReader(versionPropertiesStream, StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new UnsupportedOperationException(e);
			}

			this.engineVersion = properties.getProperty("version");
			this.lastTag = properties.getProperty("lastTag");
			this.commitDistance = properties.getProperty("commitDistance");
			this.gitHash = properties.getProperty("gitHash");
			this.gitHashFull = properties.getProperty("gitHashFull");
			this.branchName = properties.getProperty("branchName");
			this.isCleanTag = properties.getProperty("isCleanTag");
			this.buildTime = properties.getProperty("buildTime");
		}
	}
}
