package com.aiblooddiagnostics.di

import com.aiblooddiagnostics.data.api.BloodDiagnosticsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // For Android Emulator: Use 10.0.2.2 to access host machine's localhost
        // For Physical Device on same WiFi: Use 192.168.0.110
        // Try these in order if one doesn't work:
        // 1. "http://10.0.2.2:8080/" - Standard emulator
        // 2. "http://192.168.0.110:8080/" - Your PC's IP
        // 3. "http://192.168.56.1:8080/" - VirtualBox network
        
        val baseUrl = "http://10.0.2.2:8080/"
        
        android.util.Log.d("NetworkModule", "Base URL configured: $baseUrl")
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBloodDiagnosticsApi(retrofit: Retrofit): BloodDiagnosticsApi {
        return retrofit.create(BloodDiagnosticsApi::class.java)
    }
}
