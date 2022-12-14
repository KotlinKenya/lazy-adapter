# Lazy Adapter

![JitPack](https://img.shields.io/jitpack/v/github/KotlinKenya/lazy-adapter?style=for-the-badge)

A simple and minimal utility class to handle recyclerview lists

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

* Add the lazy adapter dependency

In your app module find `build.gradle.kts` and add :

```kotlin
  implementation("com.github.KotlinKenya:lazy-adapter:$version")
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
  implementation 'com.github.KotlinKenya:lazy-adapter:$version'
```

<br/>

## Usage

- let your data class extend the `LazyCompare()`
  ```kotlin
    data class Item (val name: String) : LazyCompare()
  ```
- create an xml file for your object
  ```xml
  <!-- in this example our layout is item_layout.xml  -->
  
  <?xml version="1.0" encoding="utf-8"?>
  <androidx.constraintlayout.widget.ConstraintLayout ...>

    <androidx.cardview.widget.CardView ...>

        <TextView
            android:id="@+id/tv_text"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:padding="16dp"
            android:text="TextView" />
  
    </androidx.cardview.widget.CardView>
  
  </androidx.constraintlayout.widget.ConstraintLayout>
  ```
- create an adapter from the `LazyAdapter` class
  ```kotlin
     val adapter = LazyAdapter<Item, LayoutItemBinding>()
  ```
- initialize the view
  ```kotlin
  adapter.onCreate { parent: ViewGroup -> 
    val inflater = LayoutInflater.from(parent.context)
    LayoutItemBinding.inflate(inflater, parent, false) 
  }
  ```
- bind the views
  ```kotlin
  adapter.onBind {item : Item ->
    // you can access all views in here
    tvText.text = item.name
  }
  ```
- set the adapter to the recyclerview and submitList
  ```kotlin
    recyclerview.adapter = adapter
    adapter.submitList(listOf<Item>()) // populate your list
  ```

## Contributing

![GitHub tag (latest by date)](https://img.shields.io:/github/v/tag/KotlinKenya/lazy-adapter?style=for-the-badge)
![GitHub contributors](https://img.shields.io:/github/contributors/KotlinKenya/lazy-adapter?style=for-the-badge)
![GitHub last commit](https://img.shields.io:/github/last-commit/KotlinKenya/lazy-adapter?style=for-the-badge)
[![Good first issues](https://img.shields.io/github/issues/KotlinKenya/lazy-adapter/good%20first%20issue?style=for-the-badge)](https://github.com/KotlinKenya/lazy-adapter/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22)
![GitHub issues](https://img.shields.io:/github/issues-raw/KotlinKenya/lazy-adapter?style=for-the-badge)
![GitHub pull requests](https://img.shields.io:/github/issues-pr/KotlinKenya/lazy-adapter?style=for-the-badge)

Your contributions are especially welcome. Whether it comes in the form of code patches, ideas,
discussion, bug reports, encouragement or criticism, your input is needed.

Visit [issues](https://github.com/KotlinKenya/lazy-adapter/issues) to get started.
