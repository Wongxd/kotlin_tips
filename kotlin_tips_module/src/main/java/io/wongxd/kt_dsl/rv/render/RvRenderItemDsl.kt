package io.wongxd.kt_dsl.rv.render

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout

class RvRenderItemDsl<T : RvRenderItem> {

    private var resId: Int = 0
    private var onBind: RvRenderScope<T>.() -> Unit = {}
    private var onBindPayload: RvRenderScope<T>.(payload: List<Any>) -> Unit = { _: List<Any> -> onBind() }

    private var onAttach: RvRenderScope<T>.() -> Unit = {}
    private var onDetach: RvRenderScope<T>.() -> Unit = {}
    private var onRecycled: RvRenderScope<T>.() -> Unit = {}

    private var gridSpanSize = 1
    private var staggerFullSpan = false

    /**
     * Set item res layout resource
     */
    fun res(res: Int) {
        this.resId = res
    }

    fun defaultStateView() {
        this.resId = -1
    }

    fun onBind(block: RvRenderScope<T>.() -> Unit) {
        this.onBind = block
    }

    fun onBindPayload(block: RvRenderScope<T>.(payload: List<Any>) -> Unit) {
        this.onBindPayload = block
    }

    fun onAttach(block: RvRenderScope<T>.() -> Unit) {
        this.onAttach = block
    }

    fun onDetach(block: RvRenderScope<T>.() -> Unit) {
        this.onDetach = block
    }

    fun onRecycled(block: RvRenderScope<T>.() -> Unit) {
        this.onRecycled = block
    }

    /**
     * Only work for Grid, set the span size of this item
     *
     * @param spanSize spanSize
     */
    fun gridSpanSize(spanSize: Int) {
        this.gridSpanSize = spanSize
    }

    /**
     * Only work for Stagger, set the fullSpan of this item
     *
     * @param fullSpan True or false
     */
    fun staggerFullSpan(fullSpan: Boolean) {
        this.staggerFullSpan = fullSpan
    }

    fun prepare(key: Int, adapter: RvRenderAdapter) {
        adapter.setItemBuilder(key, generateItemBuilder())
    }

    private fun generateItemBuilder(): RvRenderItemBuilder {
        return RvRenderItemBuilder(
                gridSpanSize,
                staggerFullSpan,
                ::builder
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun builder(viewGroup: ViewGroup): RvRenderViewHolder {
        val view = if (this.resId == -1) {
            val stateView = RvRenderStateView(viewGroup.context)
            val sLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            LinearLayout(viewGroup.context).apply {
                val lp = viewGroup.layoutParams
                        ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams = lp
                setPadding(0, 10.dp2px, 0, 10.dp2px)
                addView(stateView, sLp)
            }
        } else {
            LayoutInflater.from(viewGroup.context).inflate(this.resId, viewGroup, false)
        }



        return object : RvRenderViewHolder(view) {

            val viewHolderScope = RvRenderScope<T>(view)


            override fun onBind(t: RvRenderItem) {
                t as T
                viewHolderScope.data = t
                viewHolderScope.onBind()
            }

            override fun onBindPayload(t: RvRenderItem, payload: MutableList<Any>) {
                t as T
                viewHolderScope.data = t
                viewHolderScope.onBindPayload(payload)
            }

            override fun onAttach(t: RvRenderItem) {
                t as T
                viewHolderScope.data = t
                viewHolderScope.onAttach()
            }

            override fun onDetach(t: RvRenderItem) {
                t as T
                viewHolderScope.data = t
                viewHolderScope.onDetach()
            }

            override fun onRecycled(t: RvRenderItem) {
                t as T
                viewHolderScope.data = t
                viewHolderScope.onRecycled()
            }
        }
    }
}