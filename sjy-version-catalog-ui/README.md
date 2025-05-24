This module provides a centralized version catalog for managing dependencies in your Gradle project. It simplifies dependency management by defining versions and libraries in a single TOML file.

## How to Use

### 1. Add as a Git Submodule

First, add the `sjy-version-catalog-ui` repository as a submodule to your root project:

```bash
git submodule add https://github.com/Sanjaya-Inc/sjy-version-catalog-ui.git
```

### 2. Add to `settings.gradle.kts`

To include the `sjy-version-catalog-ui` version catalog in your project, add the following to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("ui") {
            from(files("sjy-version-catalog-ui/ui.versions.toml"))
        }
    }
}
```

### 3. Use Dependencies with the `ui` Prefix

In your `build.gradle.kts` file, you can reference dependencies defined in the `ui.versions.toml` file using the `ui` prefix. For example:

```kotlin
dependencies {
    implementation(ui.libs.androidx.ui.ktx)
}
```

### Benefits

- **Centralized Management**: All dependency versions are managed in a single TOML file.
- **Consistency**: Ensures consistent versions across all modules in the project.
- **Ease of Use**: Simplifies dependency declarations with aliases and bundles.

### File Structure

- `ui.versions.toml`: Contains all version definitions, library aliases, and bundles.
- `README.md`: Documentation for using the version catalog.

### Example `ui.versions.toml`

Here is an example of what the `ui.versions.toml` file might look like:

```toml
[versions]
orbit-mvi = "9.0.0"
lifecycle = "2.9.0"
activity = "1.10.1"
compose-bom = "2025.05.00"

[libraries]
orbit-mvi-core = { group = "org.orbit-mvi", name = "orbit-core", version.ref = "orbit-mvi" }
orbit-mvi-viewmodel = { group = "org.orbit-mvi", name = "orbit-viewmodel", version.ref = "orbit-mvi" }
orbit-mvi-compose = { group = "org.orbit-mvi", name = "orbit-compose", version.ref = "orbit-mvi" }

[bundles]
orbit-mvi = [
    "orbit-mvi-core",
    "orbit-mvi-viewmodel",
    "orbit-mvi-compose"
]

```
