package com.example.techtask.app.di

import android.app.Application
import androidx.room.Room
import com.example.techtask.Constants
import com.example.techtask.api.Everything
import com.example.techtask.db.AppDatabase
import com.example.techtask.db.dao.NewsDao
import com.example.techtask.db.repository.NewsRepository
import com.example.techtask.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val viewModelModule = module {
    single { MainViewModel(get()) }
}

val apiModule = module {
    fun provideNewsApi(retrofit: Retrofit): Everything {
        return retrofit.create(Everything::class.java)
    }

    single { provideNewsApi(get()) }
}

val netModule = module {
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(
            object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.tag("OkHttp").d(message)
                }
            }
        ).apply {
            setLevel(HttpLoggingInterceptor.Level.BASIC)
        }
    }

    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addNetworkInterceptor {
                val requestBuilder = it.request().newBuilder()
                requestBuilder.addHeader("x-api-key", Constants.API_KEY)
                it.proceed(requestBuilder.build())
            }
            .build()
    }

    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    single { provideLoggingInterceptor() }
    single { provideHttpClient(get()) }
    single { provideRetrofit(get()) }
}

val databaseModule = module {
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "AppDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideNewsDao(database: AppDatabase): NewsDao {
        return database.newsDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideNewsDao(get()) }
}

val repositoryModule = module {
    fun provideNewsRepository(api: Everything, newsDao: NewsDao): NewsRepository {
        return NewsRepository(api, newsDao)
    }

    single { provideNewsRepository(get(), get()) }
}