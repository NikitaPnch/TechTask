package com.example.techtask.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var currentPosition = 0

    open fun onBind(position: Int) {
        currentPosition = position
    }
}