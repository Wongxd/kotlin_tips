package io.wongxd.kt_dsl.rv.render

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.ViewGroup
import io.wongxd.kt_dsl.rv.prepare_data.RvDataMultiAdapter
import io.wongxd.kt_dsl.rv.prepare_data.RvDataSource


class RvRenderAdapter(dataSource: RvDataSource<RvRenderItem>) :
        RvDataMultiAdapter<RvRenderItem, RvRenderViewHolder>(dataSource) {

    private val itemBuilderMap = mutableMapOf<Int, RvRenderItemBuilder>()

    fun setItemBuilder(key: Int, value: RvRenderItemBuilder) {
        itemBuilderMap[key] = value
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)::class.hashCode()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvRenderViewHolder {
        val itemBuilder = itemBuilderMap[viewType]
        if (itemBuilder == null) {
            throw IllegalStateException("Not supported view type: [$viewType]!")
        } else {
            return itemBuilder.viewHolder(parent)
        }
    }

    override fun onViewAttachedToWindow(holder: RvRenderViewHolder) {
        super.onViewAttachedToWindow(holder)
        specialStaggeredGridLayout(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager

        specialGridLayout(layoutManager)
    }


    private fun specialGridLayout(layoutManager: RecyclerView.LayoutManager?) {
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return getItemBuilder(position)?.gridSpanSize ?: 1
                }
            }
        }
    }

    private fun specialStaggeredGridLayout(holder: RvRenderViewHolder) {
        val layoutParams = holder.itemView.layoutParams
        if (layoutParams != null && layoutParams is StaggeredGridLayoutManager.LayoutParams) {
            val position = holder.adapterPosition
            layoutParams.isFullSpan = getItemBuilder(position)?.staggerFullSpan ?: false
        }
    }

    private fun getItemBuilder(position: Int): RvRenderItemBuilder? {
        return itemBuilderMap[getItemViewType(position)]
    }


}