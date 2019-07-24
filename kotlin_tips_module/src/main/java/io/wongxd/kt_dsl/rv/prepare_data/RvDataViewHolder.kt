package io.wongxd.kt_dsl.rv.prepare_data

import android.support.v7.widget.RecyclerView
import android.view.View


/**
 * A base interface for all view holders supporting Android Extensions-style view access.
 */
public interface LayoutContainer {
    /** Returns the root holder view. */
    public val containerView: View?
}

open class RvDataViewHolder<T>(override val containerView: View) :
    RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    open fun onBind(t: T) {}

    open fun onBindPayload(t: T, payload: MutableList<Any>) {}

    open fun onAttach(t: T) {}

    open fun onDetach(t: T) {}

    open fun onRecycled(t: T) {}
}