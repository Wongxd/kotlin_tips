package io.wongxd.ktolin_tips_demo

import android.arch.lifecycle.MutableLiveData
import io.wongxd.kt_dsl.rv.render.RvRenderDataSource
import io.wongxd.kt_dsl.rv.render.RvRenderItem

class DemoDataSource : RvRenderDataSource(stateNoMoreDesStr = "----- 我是有底线的 -----", stateRetryDesStr = "点击重试") {
    val refresh = MutableLiveData<Boolean>()
    var page = 0

    override fun loadInitial(loadCallback: LoadCallback<RvRenderItem>) {
        page = 0

        refresh.postValue(true)

        Thread.sleep(1200)

        val headers = mutableListOf<RvRenderItem>()
        for (i in 0 until 2) {
            headers.add(HeaderItem(i))
        }

        val items = mutableListOf<RvRenderItem>()
        for (i in 0 until 10) {
            items.add(NormalItem(i, System.currentTimeMillis()))
        }

        val footers = mutableListOf<RvRenderItem>()
        for (i in 0 until 2) {
            footers.add(FooterItem(i))
        }

        addHeaders(headers, delay = true)
        addFooters(footers, delay = true)

        loadCallback.setResult(items)

        refresh.postValue(false)
    }

    override fun loadAfter(loadCallback: LoadCallback<RvRenderItem>) {
        page++

        //Mock load failed.
        //模拟加载失败.
        if (page % 3 == 0) {
            loadCallback.setResult(null)
            return
        }

        if (page == 4) {
            loadCallback.setResult(emptyList())
            return
        }

        Thread.sleep(1500)
        val items = mutableListOf<RvRenderItem>()
        for (i in page * 10 until (page + 1) * 10) {
            items.add(NormalItem(i, System.currentTimeMillis()))
        }

        loadCallback.setResult(items)
    }
}