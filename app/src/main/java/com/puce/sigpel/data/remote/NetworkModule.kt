package com.puce.sigpel.data.remote

import com.puce.sigpel.BuildConfig
import com.puce.sigpel.data.auth.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Construye los clientes Retrofit: uno autenticado contra el backend SIGPEL
 * (BuildConfig.API_BASE_URL, ver local.properties.example) y otro sin
 * interceptor de auth contra el endpoint publico de Cognito.
 */
object NetworkModule {

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    fun provideApiService(sessionManager: SessionManager): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(loggingInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun provideCognitoAuthApi(): CognitoAuthApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://cognito-idp.${BuildConfig.COGNITO_REGION}.amazonaws.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CognitoAuthApi::class.java)
    }
}
