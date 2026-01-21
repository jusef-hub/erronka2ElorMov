plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)

	id("kotlin-kapt")
	id("com.google.dagger.hilt.android")
	//SafeArgs
	id("androidx.navigation.safeargs.kotlin")
}

android {
	namespace = "com.example.elormov"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.example.elormov"
		minSdk = 24
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	viewBinding {
		enable = true
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)

	//Shared View Model
	implementation(libs.androidx.lifecycle.viewmodel.ktx)

	//Nav Component
	var navVersion = "2.9.6"
	implementation("androidx.navigation:navigation-fragment-ktx:${navVersion}")
	implementation("androidx.navigation:navigation-ui-ktx:${navVersion}")


	//DaggerHilt
	val daggerVersion = "2.48"
	implementation("com.google.dagger:hilt-android:${daggerVersion}")
	kapt("com.google.dagger:hilt-compiler:${daggerVersion}")

	//Retrofit
	val retrofitVersion = "2.9.0"
	implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
	implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

	//DataStore
	implementation("androidx.datastore:datastore-preferences:1.0.0")

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}