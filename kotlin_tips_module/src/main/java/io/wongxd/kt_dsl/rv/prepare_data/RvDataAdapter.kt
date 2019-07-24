package io.wongxd.kt_dsl.rv.prepare_data

import android.support.v7.widget.RecyclerView

abstract class RvDataAdapter<T : Any, VH : RecyclerView.ViewHolder>(
        protected open val dataSource: RvDataSource<T>
) : RecyclerView.Adapter<VH>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        dataSource.setAdapter(this)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        dataSource.setAdapter(null)
    }

    open fun getItem(position: Int): T {
        return dataSource.getItemInner(position)
    }

    override fun getItemCount(): Int {
        return dataSource.getItemCount()
    }
}