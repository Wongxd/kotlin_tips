package io.wongxd.kt_dsl.rv.prepare_data

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION

abstract class RvDataMultiAdapter<T : RvDataItem, VH : RvDataViewHolder<T>>(dataSource: RvDataSource<T>) :
    RvDataAdapter<T, VH>(dataSource) {

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBindPayload(getItem(position), payloads)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType()
    }

    override fun onViewAttachedToWindow(holder: VH) {
        holder.checkPosition {
            onAttach(getItem(it))
        }
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        holder.checkPosition {
            onDetach(getItem(it))
        }
    }

    override fun onViewRecycled(holder: VH) {
        holder.checkPosition {
            onRecycled(getItem(it))
        }
    }

    private fun <VH : RecyclerView.ViewHolder> VH.checkPosition(block: VH.(Int) -> Unit) {
        this.adapterPosition.let {
            if (it != NO_POSITION) {
                this.block(it)
            }
        }
    }
}