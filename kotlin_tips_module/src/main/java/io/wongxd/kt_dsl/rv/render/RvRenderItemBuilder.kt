package io.wongxd.kt_dsl.rv.render

import android.view.ViewGroup

class RvRenderItemBuilder(
        val gridSpanSize: Int,
        val staggerFullSpan: Boolean,
        val viewHolder: (ViewGroup) -> RvRenderViewHolder
)