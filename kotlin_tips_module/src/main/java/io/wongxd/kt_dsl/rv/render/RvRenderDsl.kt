package io.wongxd.kt_dsl.rv.render

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.VERTICAL
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.ViewGroup

class RvRenderDsl(val adapter: RvRenderAdapter) {
    private var orientation = VERTICAL
    private var reverse = false
    private var spanCount = 1

    /**
     * Set the orientation
     *
     * @param orientation   Layout orientation. VERTICAL or HORIZONTAL
     */
    fun orientation(orientation: Int) {
        this.orientation = orientation
    }

    /**
     * Set whether to reverse the list
     *
     * @param reverse When set to true, layouts from end to start.
     */
    fun reverse(reverse: Boolean) {
        this.reverse = reverse
    }

    /**
     * Set SpanCount for Grid and Stagger
     *
     * @param spanCount spanCount
     */
    fun spanCount(spanCount: Int) {
        this.spanCount = spanCount
    }

    inline fun <reified T : RvRenderItem> renderItem(block: RvRenderItemDsl<T>.() -> Unit) {
        val dsl = RvRenderItemDsl<T>()
        dsl.block()
        dsl.prepare(T::class.hashCode(), adapter)
    }

    fun initLayoutManager(target: RecyclerView, type: Int) {

        target.layoutManager = when (type) {
            LINEAR_LAYOUT -> LinearLayoutManager(target.context, orientation, reverse)
            GRID_LAYOUT -> GridLayoutManager(target.context, spanCount, orientation, reverse)
            STAGGERED_LAYOUT -> StaggeredGridLayoutManager(spanCount, orientation)
            else -> throw IllegalStateException("This should never happen!")
        }
    }

    fun setDefaultStateItem() {

        renderItem<RvRenderStateItem> {

            defaultStateView()

            onBind {
                if (containerView is ViewGroup) {
                    for (i in 0..containerView.childCount) {
                        val c = containerView.getChildAt(i)
                        if (c is RvRenderStateView) {
                            c.setState(data)
                        }
                    }
                } else if (containerView is RvRenderStateView) {
                    containerView.setState(data)
                }

            }

            gridSpanSize(spanCount)

            staggerFullSpan(true)
        }
    }
}