package io.wongxd.kt_dsl.rv.prepare_data

import android.annotation.SuppressLint
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

internal class RvDataListDiffer<T> {
    var adapter: RecyclerView.Adapter<*>? = null

    private val diffCallback = PagingDiffCallback<T>()

    private var currentList = emptyList<T>()

    // Max generation of currently scheduled runnable
    private var mMaxScheduledGeneration: Int = 0

    internal fun size() = currentList.size

    internal fun get(position: Int) = currentList[position]

    internal fun submitList(newList: List<T>, initial: Boolean = false) {
        if (initial) {
            currentList = newList
            adapter?.notifyDataSetChanged()
            return
        }

        if (newList === currentList) {
            // nothing to do
            return
        }

        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = ++mMaxScheduledGeneration

        // initial simple removeItem toList
        if (newList.isEmpty()) {
            val countRemoved = currentList.size
            currentList = emptyList()
            // notify last, after list is updated
            adapter?.notifyItemRangeRemoved(0, countRemoved)
            return
        }

        // initial simple first insert
        if (currentList.isEmpty()) {
            currentList = newList
            adapter?.notifyItemRangeInserted(0, newList.size)
            return
        }

        val oldList = currentList

        ioThread {
            val result = DslDiffUtil.calculateDiff(object : DslDiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldList.size
                }

                override fun getNewListSize(): Int {
                    return newList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return diffCallback.areItemsTheSame(
                            oldList[oldItemPosition], newList[newItemPosition]
                    )
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return diffCallback.areContentsTheSame(
                            oldList[oldItemPosition], newList[newItemPosition]
                    )
                }

                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                    return diffCallback.getChangePayload(
                            oldList[oldItemPosition], newList[newItemPosition]
                    )
                }
            })

            mainThread {
                if (mMaxScheduledGeneration == runGeneration) {
                    latchList(newList, result)
                }
            }
        }
    }

    private fun latchList(newList: List<T>, diffResult: DslDiffUtil.DiffResult) {
        // notify last, after list is updated
        currentList = newList
        adapter?.let {
            diffResult.dispatchUpdatesTo(adapter)
        }
    }

    class PagingDiffCallback<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return if (oldItem is Differ && newItem is Differ) {
                oldItem.areItemsTheSame(newItem)
            } else {
                return oldItem === newItem
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return if (oldItem is Differ && newItem is Differ) {
                oldItem.areContentsTheSame(newItem)
            } else {
                true
            }
        }

        override fun getChangePayload(oldItem: T, newItem: T): Any? {
            return if (oldItem is Differ && newItem is Differ) {
                oldItem.getChangePayload(newItem)
            } else {
                null
            }
        }
    }
}
