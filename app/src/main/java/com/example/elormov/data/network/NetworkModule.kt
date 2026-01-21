package com.example.elormov.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideElorMovApiService(): Retrofit {
		return Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	@Provides
	fun provideElorMovApi(retrofit: Retrofit): ElorMovApiService {
		return retrofit.create(ElorMovApiService::class.java)
	}
}