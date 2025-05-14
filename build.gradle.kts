plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.1.20" // Consider updating to a newer Kotlin version if 1.9.25 has issues with JDK 23
  id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.nutys.simplelog"
version = "1.0.0"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
  implementation(kotlin("stdlib"))
  intellijPlatform {
    create("IC", "2025.1.1.1") // Placeholder for IDEA 2025.1, adjust if a specific EAP build number is available
    testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

    // Add necessary plugin dependencies for compilation here, example:
    bundledPlugin("com.intellij.java")
  }
}

intellijPlatform {
  pluginConfiguration {
    ideaVersion {
      sinceBuild = "251"
    }

    changeNotes = """
      1.0.0 Support the functions of quick insertion and quick deletion
    """.trimIndent()
  }
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }
}
