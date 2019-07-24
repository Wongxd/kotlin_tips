package io.wongxd.kt_dsl.rv.render

import android.support.v7.widget.RecyclerView
import io.wongxd.kt_dsl.rv.prepare_data.RvDataSource

const val LINEAR_LAYOUT = 0
const val GRID_LAYOUT = 1
const val STAGGERED_LAYOUT = 2

/**
 * Create a linear list .
 *
 * @param dataSource The data source.
 *
 */
fun RecyclerView.linear(dataSource: RvDataSource<RvRenderItem>,
                        block: RvRenderDsl.() -> Unit) {
    initDsl(this, LINEAR_LAYOUT, dataSource, block)
}

/**
 * Create a grid list .
 *
 * @param dataSource The data source.
 *
 */
fun RecyclerView.grid(dataSource: RvDataSource<RvRenderItem>,
                      block: RvRenderDsl.() -> Unit) {
    initDsl(this, GRID_LAYOUT, dataSource, block)
}

/**
 * Create a stagger list .
 *
 * @param dataSource The data source.
 *
 */
fun RecyclerView.stagger(dataSource: RvDataSource<RvRenderItem>,
                         block: RvRenderDsl.() -> Unit) {
    initDsl(this, STAGGERED_LAYOUT, dataSource, block)
}

private fun initDsl(
        target: RecyclerView,
        type: Int,
        dataSource: RvDataSource<RvRenderItem>,
        block: RvRenderDsl.() -> Unit
) {
    val adapter = RvRenderAdapter(dataSource)

    val dsl = RvRenderDsl(adapter)
    dsl.block()
    dsl.initLayoutManager(target, type)

    target.adapter = adapter
}