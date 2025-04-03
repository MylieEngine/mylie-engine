package mylie.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.Getter;

/**
 * The Build class provides a set of static methods to retrieve metadata
 * about the build, such as engine version, Git information, and build time.
 * This metadata is loaded from a properties file at runtime.
 */
@Getter
public final class Build {
	private Build() {
	}
	private static final Info INFO = new Info();

	/**
	 * Retrieves the current engine version from the build metadata.
	 *
	 * @return the engine version as a {@link String}.
	 */
	public static String engineVersion() {
		return INFO.engineVersion();
	}

	/**
	 * Returns the last Git tag associated with the build.
	 *
	 * @return the last tag as a {@link String}.
	 */
	public static String lastTag() {
		return INFO.lastTag();
	}

	/**
	 * Provides the number of commits since the last Git tag.
	 *
	 * @return the commit distance as a {@link String}.
	 */
	public static String commitDistance() {
		return INFO.commitDistance();
	}

	/**
	 * Retrieves the abbreviated Git hash of the current build.
	 *
	 * @return the Git hash as a {@link String}.
	 */
	public static String gitHash() {
		return INFO.gitHash();
	}

	/**
	 * Retrieves the full Git hash of the current build.
	 *
	 * @return the full Git hash as a {@link String}.
	 */
	public static String gitHashFull() {
		return INFO.gitHashFull();
	}

	/**
	 * Returns the Git branch name for the current build.
	 *
	 * @return the branch name as a {@link String}.
	 */
	public static String branchName() {
		return INFO.branchName();
	}

	/**
	 * Indicates whether the current build is based on a clean Git tag.
	 *
	 * @return "true" if the build is clean, otherwise "false", as a {@link String}.
	 */
	public static String isCleanTag() {
		return INFO.isCleanTag();
	}

	/**
	 * Provides the timestamp of when the build was created.
	 *
	 * @return the build time as a {@link String}.
	 */
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
