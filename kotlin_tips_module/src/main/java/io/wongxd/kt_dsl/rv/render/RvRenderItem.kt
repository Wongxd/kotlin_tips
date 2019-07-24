package io.wongxd.kt_dsl.rv.render

import android.view.View
import io.wongxd.kt_dsl.rv.prepare_data.LayoutContainer
import io.wongxd.kt_dsl.rv.prepare_data.RvDataItem
import io.wongxd.kt_dsl.rv.prepare_data.RvDataViewHolder

interface RvRenderItem : RvDataItem


class RvRenderScope<T : RvRenderItem>(override val containerView: View) : LayoutContainer {
    lateinit var data: T
}


class RvRenderStateItem(val state: Int, val retry: () -> Unit, val stateDes: String) : RvRenderItem


open class RvRenderViewHolder(containerView: View) : RvDataViewHolder<RvRenderItem>(containerView)

