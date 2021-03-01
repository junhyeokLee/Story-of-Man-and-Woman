package com.dev_sheep.story_of_man_and_woman.utils

import androidx.recyclerview.widget.DiffUtil
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed

class Diff(
    private val oldItems: List<Feed>,
    private val newItems: List<Feed>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int =
        oldItems.size

    override fun getNewListSize(): Int =
        newItems.size

    /**
     * 고유 값을 비교하는게 좋다.
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return oldItem.feed_seq == newItem.feed_seq
    }

    /**
     * 아이템을 서로 비교하는게 좋다.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return oldItem == newItem
    }
}