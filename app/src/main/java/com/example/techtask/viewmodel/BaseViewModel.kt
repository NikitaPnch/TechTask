package com.example.techtask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {

    val bus: PublishSubject<Actions> = PublishSubject.create()

    private suspend fun send(action: Actions) {
        runCatching {
            bus.onNext(action)
            listen(action)
        }.onFailure { t ->
            Actions.Error(t).let {
                bus.onNext(it)
                listen(it)
            }
        }
    }

    suspend fun sendAwait(action: suspend () -> Actions) {
        return withContext(Dispatchers.Main) {
            send(action())
        }
    }

    fun send(action: suspend () -> Actions) {
        viewModelScope.launch(Dispatchers.Main) {
            sendAwait(action)
        }
    }

    protected open suspend fun listen(action: Actions) {}

    inline fun <reified T : Any> listen(): Observable<T> = bus
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .ofType(T::class.java)
}