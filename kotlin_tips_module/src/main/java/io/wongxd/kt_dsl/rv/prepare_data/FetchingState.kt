package io.wongxd.kt_dsl.rv.prepare_data

class FetchingState {
    companion object {
        const val READY_TO_FETCH = 0
        const val FETCHING = 1
        const val DONE_FETCHING = 2
        const val NO_MORE_DATA = 3
        const val FETCHING_ERROR = 4
    }

    private var loadState = READY_TO_FETCH

    fun isNotReady(): Boolean {
        return loadState != READY_TO_FETCH
    }

    fun setState(state: Int) {
        this.loadState = state
    }
}