package com.flacrow.showtracker.data.di

import com.flacrow.showtracker.api.ShowAPI
import com.flacrow.showtracker.utils.Config
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
object NetworkModule {

    @Provides
    @AppScope
    fun provideOkhttp(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)
        return builder.build()
    }

    @Provides
    @AppScope
    fun provideRetrofitService(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @AppScope
    fun provideShowApi(retrofit: Retrofit): ShowAPI {
        return retrofit.create(ShowAPI::class.java)
    }
}