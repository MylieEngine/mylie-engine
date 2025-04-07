package mylie.engine;

import static mylie.engine.util.FileUtils.*;

import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mylie.engine.util.exceptions.IllegalInstantiationException;

/**
 * The Build class provides a set of static methods to retrieve metadata
 * about the build, such as engine version, Git information, and build time.
 * This metadata is loaded from a properties file at runtime.
 */
@Slf4j
@Getter
public final class Build {
	private Build() {
		throw new IllegalInstantiationException(Build.class);
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
	private static final class Info {
		private static final String UNKNOWN = "unknown";
		private final Properties BUILD_INFO;

		private final String engineVersion;
		private final String lastTag;
		private final String commitDistance;
		private final String gitHash;
		private final String gitHashFull;
		private final String branchName;
		private final String isCleanTag;
		private final String buildTime;

		private Info() {
			BUILD_INFO = loadProperties("/mylie/engine/version.properties");
			engineVersion = BUILD_INFO.getProperty("version", UNKNOWN);
			lastTag = BUILD_INFO.getProperty("lastTag", UNKNOWN);
			commitDistance = BUILD_INFO.getProperty("commitDistance", UNKNOWN);
			gitHash = BUILD_INFO.getProperty("gitHash", UNKNOWN);
			gitHashFull = BUILD_INFO.getProperty("gitHashFull", UNKNOWN);
			branchName = BUILD_INFO.getProperty("branchName", UNKNOWN);
			isCleanTag = BUILD_INFO.getProperty("isCleanTag", UNKNOWN);
			buildTime = BUILD_INFO.getProperty("buildTime", UNKNOWN);
		}
	}
}
