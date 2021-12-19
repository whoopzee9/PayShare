package ru.spbstu.common.di

import android.content.Context
import android.content.SharedPreferences
import retrofit2.Retrofit
import ru.spbstu.common.token.TokenRepository
import ru.spbstu.common.utils.BundleDataWrapper
import javax.inject.Named

interface CommonApi {

    fun context(): Context

    fun provideSharedPreferences(): SharedPreferences

    fun provideRetrofit(): Retrofit

    fun provideBundleDataWrapper(): BundleDataWrapper

    fun provideTokenRepository(): TokenRepository

    @Named("encrypted")
    fun provideEncryptedPreferences(): SharedPreferences
}
