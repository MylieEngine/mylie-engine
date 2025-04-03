plugins {
    alias(libs.plugins.spotless)
}

group = "mylie-engine"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    group = rootProject.group
    version = rootProject.version
    repositories {
        mavenCentral()
    }

    afterEvaluate {
        plugins.withId("java-base") {
            apply(plugin = libs.plugins.spotless.get().pluginId)
            dependencies {
                val compileOnly by configurations
                val annotationProcessor by configurations
                val api by configurations

                api(libs.logging.api)
                compileOnly(libs.lombok)
                annotationProcessor(libs.lombok)
            }

            spotless {
                java {
                    removeUnusedImports()
                    importOrder()
                    eclipse().configFile(rootProject.file("JavaFormatRules.xml"))
                    formatAnnotations()
                    trimTrailingWhitespace()
                    endWithNewline()
                }
            }
        }
    }
}