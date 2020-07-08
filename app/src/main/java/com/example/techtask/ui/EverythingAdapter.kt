package com.example.techtask.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.techtask.Events
import com.example.techtask.R
import com.example.techtask.db.DBNews
import com.example.techtask.extensions.createImageRequest
import com.example.techtask.extensions.getDate
import com.example.techtask.extensions.getTimestampFromString
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.rxjava3.subjects.PublishSubject

class EverythingAdapter(private val busEvent: PublishSubject<Any>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    companion object {
        private const val IS_NORMAL = 0
        private const val IS_LOADING = 1
        private const val IS_ERROR = 2
    }

    private var articles: MutableList<DBNews> = mutableListOf()
    private lateinit var recyclerView: RecyclerView

    private var isLoaderVisible = false
    private var isErrorVisible = false

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val articleContainer: ConstraintLayout =
            itemView.findViewById(R.id.cl_news_item_container)
        private val articleImage: SimpleDraweeView = itemView.findViewById(R.id.sdv_image_news)
        private val articleHeader: TextView = itemView.findViewById(R.id.tv_header_news)
        private val articleDescription: TextView = itemView.findViewById(R.id.tv_news_description)
        private val divider: View = itemView.findViewById(R.id.view_item_divider)
        private val dateText: TextView = itemView.findViewById(R.id.tv_date)

        override fun onBind(position: Int) {
            super.onBind(position)
            val article = articles[position]

            article.apply {
                setupArticleImage(articleImage)
                setupArticleDivider(divider)
                setupArticleHeader(articleHeader)
                setupArticleDescription(articleDescription)
                setupClicksArticleContainer(articleContainer)
                setupDateText(dateText)
            }
        }
    }

    inner class ProgressHolder(itemView: View) : BaseViewHolder(itemView)
    inner class ErrorHolder(itemView: View) : BaseViewHolder(itemView) {

        private val text: TextView = itemView.findViewById(R.id.error_text_repeat)

        override fun onBind(position: Int) {
            super.onBind(position)

            text.setOnClickListener {
                busEvent.onNext(Events.LoadNewsAfterError())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            IS_NORMAL -> {
                ViewHolder(
                    inflater.inflate(
                        R.layout.item_everything, parent, false
                    )
                )
            }
            IS_LOADING -> {
                ProgressHolder(
                    inflater.inflate(
                        R.layout.item_loading, parent, false
                    )
                )
            }
            IS_ERROR -> {
                ErrorHolder(
                    inflater.inflate(
                        R.layout.item_error, parent, false
                    )
                )
            }
            else -> {
                ViewHolder(
                    inflater.inflate(
                        R.layout.item_everything, parent, false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoaderVisible && position == articles.size - 1 -> IS_LOADING
            isErrorVisible && position == articles.size - 1 -> IS_ERROR
            else -> IS_NORMAL
        }
    }

    // показать item загрузки
    fun addLoading() {
        isLoaderVisible = true
        articles.add(DBNews())
        notifyItemInserted(articles.size - 1)
    }

    // убрать item загрузки
    fun removeLoading() {
        isLoaderVisible = false
        val position = articles.size - 1
        if (position > -1) {
            articles.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // показать ошибки
    fun addError() {
        isErrorVisible = true
        articles.add(DBNews())
        notifyItemInserted(articles.size - 1)
    }

    // убрать ошибку
    fun removeError() {
        isErrorVisible = false
        val position = articles.size - 1
        if (position > -1) {
            articles.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // установка данных
    fun setData(articles: List<DBNews>) {
        this.articles.addAll(articles)
        notifyDataSetChanged()
    }

    // привязывает adapter к текущему recycler
    fun appendTo(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        scrollListener: RecyclerView.OnScrollListener
    ) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = layoutManager
        this.recyclerView.adapter = this
        this.recyclerView.addOnScrollListener(scrollListener)
    }

    // устанавливает изображение новости
    private fun DBNews.setupArticleImage(articleImage: SimpleDraweeView) {
        if (urlToImage.isNullOrBlank()) {
            articleImage.visibility = View.GONE
        } else {
            articleImage.visibility = View.VISIBLE
            articleImage.createImageRequest(urlToImage!!)
        }
    }

    // устанавливает разделитель новостей
    private fun DBNews.setupArticleDivider(divider: View) {
        if (articles.last() == this) {
            divider.visibility = View.INVISIBLE
        } else {
            divider.visibility = View.VISIBLE
        }
    }

    // устанавливает заголовок новости
    private fun DBNews.setupArticleHeader(articleHeader: TextView) {
        articleHeader.text = title
    }

    // устанавливает описание новости новости
    private fun DBNews.setupArticleDescription(articleDescription: TextView) {
        if (description.isNullOrBlank()) {
            articleDescription.visibility = View.GONE
        } else {
            articleDescription.visibility = View.VISIBLE
            articleDescription.text = description
        }
    }

    // устанавливает нажатие по новости
    private fun DBNews.setupClicksArticleContainer(articleContainer: ConstraintLayout) {
        articleContainer.setOnClickListener { busEvent.onNext(Events.ArticleClickEvent(url)) }
    }

    // устанавливает нажатие по новости
    private fun DBNews.setupDateText(dateText: TextView) {
        dateText.text = getDate(getTimestampFromString(publishedAt))
    }
}