package com.github.watabee.qiitacompose.di

import android.content.Context
import com.github.watabee.qiitacompose.api.QiitaApiService
import com.github.watabee.qiitacompose.api.interceptor.AccessTokenInterceptor
import com.github.watabee.qiitacompose.util.OffsetDateTimeAdapter
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECT_TIMEOUT_SECONDS = 10L

private const val READ_TIMEOUT_SECONDS = 10L

private const val MAX_CACHE_SIZE = 50L * 1024 * 1024

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModule {

    @Binds
    @IntoSet
    abstract fun bindAccessTokenInterceptor(instance: AccessTokenInterceptor): Interceptor

    companion object {
        @Base
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        @Api
        @Provides
        @Singleton
        fun provideOkHttpClientForApi(
            @ApplicationContext context: Context,
            @Base okHttpClient: OkHttpClient,
            interceptors: Set<@JvmSuppressWildcards Interceptor>,
        ): OkHttpClient = okHttpClient
            .newBuilder()
            .cache(Cache(File(context.cacheDir, "api"), MAX_CACHE_SIZE))
            .apply { interceptors.forEach { addInterceptor(it) } }
            .build()

        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder().add(OffsetDateTime::class.java, OffsetDateTimeAdapter).build()

        @Provides
        @Singleton
        fun provideRetrofit(@Api okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
            .baseUrl("https://qiita.com")
            .client(okHttpClient)
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        @Provides
        @Singleton
        fun provideQiitaApiService(retrofit: Retrofit): QiitaApiService = retrofit.create(QiitaApiService::class.java)
    }
}
