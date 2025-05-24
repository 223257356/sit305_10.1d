This module provides a centralized version catalog for managing dependencies in your Gradle project. It simplifies dependency management by defining versions and libraries in a single TOML file.

## How to Use

### 1. Add as a Git Submodule

First, add the `sjy-version-catalog-core` repository as a submodule to your root project:

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-version-catalog-core.git
```

### 2. Add to `settings.gradle.kts`

To include the `sjy-version-catalog-core` version catalog in your project, add the following to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("core") {
            from(files("sjy-version-catalog-core/core.versions.toml"))
        }
    }
}
```

### 3. Use Dependencies with the `core` Prefix

In your `build.gradle.kts` file, you can reference dependencies defined in the `core.versions.toml` file using the `core` prefix. For example:

```kotlin
dependencies {
    // AndroidX
    implementation(core.libs.androidx.core.ktx)

    // Coroutines
    implementation(core.bundles.coroutines)

    // Dependency Injection - Koin
    implementation(platform(core.libs.koin.bom))
    implementation(core.libs.koin.core)
    implementation(core.libs.koin.android)

    // Testing
    testImplementation(core.bundles.koin.test)
}
```

### Benefits

- **Centralized Management**: All dependency versions are managed in a single TOML file.
- **Consistency**: Ensures consistent versions across all modules in the project.
- **Ease of Use**: Simplifies dependency declarations with aliases and bundles.

### File Structure

- `core.versions.toml`: Contains all version definitions, library aliases, and bundles.
- `README.md`: Documentation for using the version catalog.

### Example `core.versions.toml`

Here is an example of what the `core.versions.toml` file might look like:

```toml
[versions]
kotlin = "2.1.20"
coroutines = "1.10.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "kotlin" }
coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }

[bundles]
coroutines = [
    "coroutines-core"
]
```
