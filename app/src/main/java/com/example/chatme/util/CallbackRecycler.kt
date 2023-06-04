package com.example.chatme.util

import androidx.recyclerview.widget.DiffUtil

class CallbackRecycler(
    private val oldItem: List<Any>,
    private val newItem: List<Any>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
       return oldItem.size
    }

    override fun getNewListSize(): Int {
        return newItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition]==newItem[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem[oldItemPosition]==newItem[newItemPosition]
    }
}