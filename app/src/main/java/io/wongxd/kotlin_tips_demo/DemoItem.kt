package io.wongxd.kotlin_tips_demo

import io.wongxd.kt_dsl.rv.prepare_data.Differ
import io.wongxd.kt_dsl.rv.render.RvRenderItem

class NormalItem(var id: Int, var time: Long) : RvRenderItem {
    override fun toString() = "{Item $id, time $time}"

    override fun areItemsTheSame(other: Differ): Boolean {
        if (other is NormalItem) {
            return id == other.id
        }
        return super.areItemsTheSame(other)
    }

    override fun areContentsTheSame(other: Differ): Boolean {
        if (other is NormalItem) {
            if (other.time != time) {
                return false
            }
        }
        return super.areContentsTheSame(other)
    }


    override fun getChangePayload(other: Differ): Any? {

        if (other is NormalItem && other.time != time) {
            return other
        }
        return super.getChangePayload(other)
    }


}

class HeaderItem(val i: Int) : RvRenderItem {
    override fun toString() = "Header $i"
}

class FooterItem(val i: Int) : RvRenderItem {
    override fun toString() = "Footer $i"
}
