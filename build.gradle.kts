plugins {
    alias(libs.plugins.spotless)
    jacoco
    id("jacoco-report-aggregation")
}

group = "mylie-engine"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Coverage reports"
    description = "Generates an aggregate report from all subprojects"
    dependsOn(subprojects.map { it.tasks.named("test") })

    additionalSourceDirs.from(subprojects.map { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    sourceDirectories.from(subprojects.map { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    classDirectories.from(subprojects.map { it.the<SourceSetContainer>()["main"].output })
    executionData.from(subprojects.map { it.tasks.named<JacocoReport>("jacocoTestReport").get().executionData })

    reports {
        xml.required.set(true)
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
    repositories {
        mavenCentral()
    }

    tasks.withType(JacocoReport::class.java).all {
        reports {
            xml.required.set(true)
        }
    }

    tasks.withType<Test>().configureEach {
        finalizedBy(tasks.withType<JacocoReport>())
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