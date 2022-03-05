# package.json

Simple Kotlin MPP module declaring [NPMs `package.json`](https://docs.npmjs.com/cli/v8/configuring-npm/package-json) as a [Kotlinx Serializable](https://github.com/Kotlin/kotlinx.serialization) structure.

### Gradle Dependency

```kotlin
implementation("com.sugarmanz.npm:package-json:$version")
```

### Basic Usage

Parsing a `package.json`:

```kotlin
val myPackage: PackageJson = Json.decodeFromString("""
{
  "name": "my-package",
  // ...
}
""")
```

Adding a dependency:

```kotlin
myPackage.dependencies.add("name", "0.0.0")
```

Writing back to JSON:

```kotlin
Json.encodeToString(myPackage)
```
