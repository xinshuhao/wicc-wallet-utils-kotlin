package com.waykichain.wallet.client

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.waykichain.wallet.model.baas.AccointInfo
import com.waykichain.wallet.model.baas.parameter.BaseBean
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

abstract class ApiClientGenerator<T>(baseUrl: String) {
    private val time_out: Long = 15//超时时间
    var apiService: T

    init {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val builder = chain.request().newBuilder()
                    val build = builder.build()
                    chain.proceed(build)
                }
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(time_out, TimeUnit.SECONDS)
                .readTimeout(time_out, TimeUnit.SECONDS)
                .build()

        apiService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(buildGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
                .create(getApiService())
    }

    abstract fun getApiService(): Class<T>
    private fun buildGson(): Gson {
        return GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create()
    }

    fun getService(): T {
        return apiService
    }
}


fun <T : BaseBean> Observable<T>.mSubscribe(
        onSuccess: (T) -> Unit) {
    this.subscribe(object : Observer<T> {
        override fun onComplete() {
        }
        override fun onSubscribe(d: Disposable) {

        }
        override fun onNext(t: T) {
            if (t.code == 0) {
                onSuccess.invoke(t)
            } else {
                if (!t.msg.isNullOrEmpty()) {
                    t.msg?.let { }
                } else {

                }
            }
        }
        override fun onError(e: Throwable) {
        }
    })
}