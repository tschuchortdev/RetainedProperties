# Retained Properties
This small Kotlin library adds delegated properties that leverage the Arch ViewModels to survive configuration changes in Android:

```kotlin

val presenter by retained { Presenter() }

````

## Motivation

In Android when a configuration change occurs (like rotating the screen, changing the language etc) the whole activity, all fragments, views and so on are destroyed and completely recreated, causing all your member variables in the activity to lose their state. This is particularly annoying for objects that _have_ to survive screen orientation like background tasks. By using `retained` delegated properties, the variables will not reset and you don't have to worry about saving&restoring them anymore.  


### "Keeping state in the activity is a bad idea anyway, so why should I use this when I can just make my presenter extend `ViewModel`?" 

Retained delegated Properties can even be useful when you only need to persist a single variable (your presenter). It puts the burden of dealing with config changes on the activity (where it should be) and completely removes all android-lifecycle related code from your presentation layer, which can now be tested or reused more easily. 
Furthermore, `arch.lifecycle.ViewModel`s are not composition-friendly. Since your presenter _has_ to extend `ViewModel` it prevents you from extending any other class. Retained properties also give you more control over the instantiation of your presenter. You no longer have to create weird `ViewModelProvider.Factory`s and can keep your object creation android agnostic:

```kotlin

@Inject lateinit var createPresenter: (String) -> Presenter
val presenter by retained { createPresenter(intent!!.getParcelableExtra("someString")) }
````


# How to get it

In your root `build.gradle` file add:

````
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }  // <--- add this line
	}
}
````

Then in your app module's `build.gradle` file add:

````
dependencies {
	compile 'com.github.tschuchortdev:retainedproperties:1.0.0'  // <--- add this line
}
````
