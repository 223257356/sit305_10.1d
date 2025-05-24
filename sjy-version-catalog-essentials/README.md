This module provides a centralized version catalog for managing dependencies in your Gradle project. It simplifies dependency management by defining versions and libraries in a single TOML file.

## How to Use

### 1. Add as a Git Submodule

First, add the `sjy-version-catalog-essentials` repository as a submodule to your root project:

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-version-catalog-essentials.git
```

### 2. Add to `settings.gradle.kts`

To include the `sjy-version-catalog-essentials` version catalog in your project, add the following to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("essentials") {
            from(files("sjy-version-catalog-essentials/essentials.versions.toml"))
        }
    }
}
```

### 3. Use Dependencies with the `essentials` Prefix

In your `build.gradle.kts` file, you can reference dependencies defined in the `essentials.versions.toml` file using the `essentials` prefix. For example:

```kotlin
dependencies {
    // AndroidX
    implementation(essentials.libs.androidx.essentials.ktx)

    // Coroutines
    implementation(essentials.bundles.coroutines)

    // Dependency Injection - Koin
    implementation(platform(essentials.libs.koin.bom))
    implementation(essentials.libs.koin.essentials)
    implementation(essentials.libs.koin.android)

    // Testing
    testImplementation(essentials.bundles.koin.test)
}
```

### Benefits

- **Centralized Management**: All dependency versions are managed in a single TOML file.
- **Consistency**: Ensures consistent versions across all modules in the project.
- **Ease of Use**: Simplifies dependency declarations with aliases and bundles.

### File Structure

- `essentials.versions.toml`: Contains all version definitions, library aliases, and bundles.
- `README.md`: Documentation for using the version catalog.

### Example `essentials.versions.toml`

Here is an example of what the `essentials.versions.toml` file might look like:

```toml
[versions]
kotlin = "2.1.20"
coroutines = "1.10.1"

[libraries]
androidx-essentials-ktx = { group = "androidx.essentials", name = "essentials-ktx", version.ref = "kotlin" }
coroutines-essentials = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-essentials", version.ref = "coroutines" }

[bundles]
coroutines = [
    "coroutines-essentials"
]
```
