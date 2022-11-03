# IMAGE PICKER

![JitPack](https://img.shields.io/jitpack/v/github/tabasumu/image-picker?style=for-the-badge)

A simple and minimal image picker and image cropping library for android

[Installation](#installation) •
[Usage](#usage) •
[Contributing](#contributing) •

## Installation ([Kotlin DSL](#kotlin-dsl) • [Groovy](#groovy) )

### Kotlin DSL

* Install `jitpack`

Locate your `build.gradle.kts` file in the root project and add :

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") } // add this line
    }
}
```

For those with a higher gradle version, find `settings.gradle.kts` in the root project folder and
add :

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") } // add this line
    }
}
```

* Add Image Picker Dependency

In your app module find `build.gradle.kts` and add :

```kotlin
  implementation("com.github.tabasumu:image-picker:$version")
```

* Sync gradle and proceed use the library

### Groovy

* Install `jitpack`

Locate your `build.gradle` file in the root project and add :

``` groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" } // add this line
    }
}
```

For those with a higher gradle version, find `settings.gradle` and add :

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // add this line
    }
}
```

* Add Image Picker dependency

In your app module find `build.gradle` and add :

```groovy
  implementation 'com.github.tabasumu:image-picker:$version'
```

<br/>

## Usage

- create file_path.xml file under `/res/xml`
  - if `res/xml` does not exist create right click on `res` folder and select ` New ` then ` Android Resource Directory ` and select ` xml ` under `Resource Type`
- add the snippet below to `file_path.xml` file created above
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path
        name="images"
        path="." />
</paths>
```
- in the apps' `AndroidManifest.xml` file add the following snippet referencing the ` file_path.xml ` created in step one above
```xml
<application>
    
    ...
    <!-- add this part inside application tag -->
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
  
</application>

```

- Now you can use the library like below
```kotlin

        ImagePicker.Builder(this)
            .isCropping(true) // set to false by default
            .cropType(ImagePicker.CropType.FREE)  // set to free by default
            .pickFrom(ImagePicker.PickFrom.ALL)  // set to all by default
            .resultUri { uri: Uri, file: File ->
                // use uri or file depending on your needs
            }.show()

```

```java

        new ImagePicker.Builder(this)
                .isCropping(true) // set to false by default
                .cropType(ImagePicker.CropType.FREE) // set to free by default
                .pickFrom(ImagePicker.PickFrom.ALL)  // set to all by default
                .resultUri { uri: Uri, file: File ->
                    // use uri or file depending on your needs
                }.show()

```

## Contributing

![GitHub tag (latest by date)](https://img.shields.io:/github/v/tag/tabasumu/image-picker?style=for-the-badge)
![GitHub contributors](https://img.shields.io:/github/contributors/tabasumu/image-picker?style=for-the-badge)
![GitHub last commit](https://img.shields.io:/github/last-commit/tabasumu/image-picker?style=for-the-badge)
[![Good first issues](https://img.shields.io/github/issues/tabasumu/image-picker/good%20first%20issue?style=for-the-badge)](https://github.com/tabasumu/image-picker/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22)
![GitHub issues](https://img.shields.io:/github/issues-raw/tabasumu/image-picker?style=for-the-badge) 
![GitHub pull requests](https://img.shields.io:/github/issues-pr/tabasumu/image-picker?style=for-the-badge)

Your contributions are especially welcome. Whether it comes in the form of code patches, ideas,
discussion, bug reports, encouragement or criticism, your input is needed.

Visit [issues](https://github.com/tabasumu/image-picker/issues) to get started.

Does the following

- get image from gallery or camera
- crops image
- returns uri or file path