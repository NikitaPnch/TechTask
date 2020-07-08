package com.example.techtask.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import autodispose2.androidx.lifecycle.scope
import autodispose2.autoDispose
import com.example.techtask.Constants
import com.example.techtask.Events
import com.example.techtask.R
import com.example.techtask.extensions.ConnectionLiveData
import com.example.techtask.extensions.debounce
import com.example.techtask.extensions.liveDataNotNull
import com.example.techtask.extensions.observeNotNull
import com.example.techtask.viewmodel.Actions
import com.example.techtask.viewmodel.MainActions
import com.example.techtask.viewmodel.MainViewModel
import io.reactivex.rxjava3.kotlin.ofType
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val model by viewModel<MainViewModel>()
    private val busEvent = PublishSubject.create<Any>()
    private lateinit var everythingAdapter: EverythingAdapter
    private var isErrorShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecycler()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()

        setupBusEvents()
    }

    // устанавливает слушатель ивентов шины
    private fun setupBusEvents() {

        // открывает новость в webview
        busEvent.ofType<Events.ArticleClickEvent>()
            .doOnError {
                Timber.tag("ERROR").e(it.localizedMessage)
            }
            .autoDispose(scope())
            .subscribe {
                val customTabsIntent = CustomTabsIntent.Builder().apply {
                    addDefaultShareMenuItem()
                    setToolbarColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.colorWhite
                        )
                    )
                }.build()
                customTabsIntent.launchUrl(this, Uri.parse(it.url))
            }

        // загружает новости после того как произошла ошибка
        busEvent.ofType<Events.LoadNewsAfterError>()
            .doOnError {
                Timber.tag("ERROR").e(it.localizedMessage)
            }
            .autoDispose(scope())
            .subscribe {
                isErrorShow = false
                everythingAdapter.removeError()
                everythingAdapter.addLoading()
                getEverything()
            }
    }

    // устанавливает слушатели
    private fun setupListeners() {

        // устанавливает слушатель интернет соединения
        ConnectionLiveData(this)
            .distinctUntilChanged()
            .debounce()
            .observeNotNull(this) { isConnected ->
                isConnected?.let {
                    showOrRemoveError(isConnected, model.hasNext.value ?: false)
                    if (isConnected) {
                        getEverything()
                    }
                }
            }

        // слушает update новостей из базы
        model.news.observeNotNull(this) {
            everythingAdapter.removeLoading()
            everythingAdapter.setData(it)
            model.hasNext.value?.let { hasNext ->
                if (hasNext) {
                    everythingAdapter.addLoading()
                }
            }
        }

        // слушает ошибку загрузки новостей
        model.listen<Actions.Error>().liveDataNotNull(this) {
            if (!isErrorShow) {
                everythingAdapter.removeLoading()
                everythingAdapter.addError()
            }
        }
    }

    // отправляет запрос в VM на получение новостей
    private fun getEverything() {
        model.send { MainActions.GetEverything() }
    }

    // показать или скрыть ошибку
    private fun showOrRemoveError(isConnected: Boolean, hasNext: Boolean) {
        when {
            hasNext && !isConnected -> {
                isErrorShow = true
                everythingAdapter.removeLoading()
                everythingAdapter.addError()
            }
            !hasNext && isConnected -> {
                isErrorShow = false
                everythingAdapter.removeError()
            }
            !hasNext && !isConnected -> {
                isErrorShow = true
                everythingAdapter.addError()
            }
        }
    }

    // настраивает Recycler
    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this)
        val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (model.isLoading.value == false && model.hasNext.value == true) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0 && totalItemCount >= Constants.PAGE_SIZE
                        ) {
                            getEverything()
                        }
                    }
                }
            }
        everythingAdapter = EverythingAdapter(busEvent)
        everythingAdapter.appendTo(rv_everything_news, layoutManager, recyclerViewOnScrollListener)
    }
}