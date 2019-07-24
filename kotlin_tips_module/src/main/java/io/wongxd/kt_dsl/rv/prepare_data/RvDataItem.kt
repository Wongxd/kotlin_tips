package io.wongxd.kt_dsl.rv.prepare_data

interface Differ {
    fun areItemsTheSame(other: Differ): Boolean {
        return this === other
    }

    fun areContentsTheSame(other: Differ): Boolean {
        return true
    }

    fun getChangePayload(other: Differ): Any? {
        return null
    }
}

interface ViewType {
    fun viewType(): Int {
        return 0
    }
}

interface RvDataItem : Differ, ViewType