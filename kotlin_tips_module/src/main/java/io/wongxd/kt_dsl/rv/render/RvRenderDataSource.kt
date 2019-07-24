package io.wongxd.kt_dsl.rv.render

import io.wongxd.kt_dsl.rv.prepare_data.FetchingState
import io.wongxd.kt_dsl.rv.prepare_data.RvHeaderAndFooterDataSource


open class RvRenderDataSource(
        private val stateRetryDesStr: String = "retry",
        private val stateNoMoreDesStr: String = "no more data",
        private val stateReTry: (() -> Unit)? = null) : RvHeaderAndFooterDataSource<RvRenderItem>() {


    override fun onStateChanged(newState: Int) {
        val des = if (newState == FetchingState.NO_MORE_DATA) stateNoMoreDesStr else stateRetryDesStr
        setState(RvRenderStateItem(newState, stateReTry ?: ::retry, des))
    }
}