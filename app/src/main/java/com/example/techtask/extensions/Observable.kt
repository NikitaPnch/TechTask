package com.example.techtask.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable

fun <T> Observable<T>.liveData(strategy: BackpressureStrategy = BackpressureStrategy.BUFFER) =
    LiveDataReactiveStreams.fromPublisher<T>(this.toFlowable(strategy))

fun <T> Observable<T>.liveData(lifecycleOwner: LifecycleOwner, observer: (T?) -> Unit) =
    liveData().observe(lifecycleOwner) { observer(it) }

fun <T> Observable<T>.liveDataNotNull(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) =
    liveData().observe(lifecycleOwner) { it?.let(observer) }