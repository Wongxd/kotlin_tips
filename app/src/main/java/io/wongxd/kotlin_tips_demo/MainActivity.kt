package io.wongxd.kotlin_tips_demo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import io.wongxd.kotlin_tips.log.KtNetLog
import io.wongxd.kotlin_tips.log.KtUiLog
import io.wongxd.kotlin_tips.startAty
import io.wongxd.kotlin_tips.update.UpdateInfo
import io.wongxd.kotlin_tips.update.UpdateUtil
import io.wongxd.kotlin_tips.update.UpdateUtilOnDownloadListener
import io.wongxd.kt_dsl.rv.render.linear
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val demoViewModel by lazy { ViewModelProviders.of(this)[DemoViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KtUiLog.init(this, true)
        KtNetLog.init(this, true)


        rv.linear(demoViewModel.dataSource) {

            setDefaultStateItem()

            renderItem<NormalItem> {
                res(R.layout.item_simple)
                onBind {
                    val tvInfo = containerView.findViewById<TextView>(R.id.tv)
                    tvInfo.text = "info--${data.id}-${data.time}"
                    containerView.setOnClickListener {
                        demoViewModel.dataSource.apply {
                            val newItem = NormalItem(data.id, System.currentTimeMillis())
                            setItem(data, newItem)
                        }
                    }
                }


            }


            renderItem<HeaderItem> {
                res(R.layout.item_header)
                onBind {
                    containerView.setOnClickListener { startAty<AtyClock>() }
                }
            }

            renderItem<FooterItem> {
                res(R.layout.item_footer)
                onBind {

                    containerView.findViewById<TextView>(R.id.tv)
                    containerView.setOnClickListener {

                        val updateInfo = UpdateInfo().apply {
                            info = "1.上线了极力要求以至于无法再拒绝的收入功能\n" +
                                    "2.出行的二级分类加入了地铁、地铁、地铁\n" +
                                    "3.「关于」新增应用商店评分入口，你们知道怎么做\n" +
                                    "4.「关于」还加入了GitHub地址，情怀+1s\n" +
                                    "5.全新的底层适配框架，优化更多机型"
                            ver = "v2.5"
                            downloadUrl = "http://paywhere.kongzue.com/downloads/paywhere.apk"
                        }

                        val updateUtil = UpdateUtil(this@MainActivity, BuildConfig.APPLICATION_ID)

                        updateUtil.setOnDownloadListener(object : UpdateUtilOnDownloadListener {
                            override fun onStart(downloadId: Long) {
                                Log.i("MainActivity", "onStart: 下载开始")
                            }

                            override fun onDownloading(downloadId: Long, progress: Int) {
                                Log.i("MainActivity", "onStart: 下载中：$progress")
                            }

                            override fun onSuccess(downloadId: Long) {
                                Log.i("MainActivity", "onStart: 下载完成")
                            }

                            override fun onCancel(downloadId: Long) {
                                Log.i("MainActivity", "onStart: 下载取消")
                            }

                        })

                        updateUtil.showNormalUpdateDialog(updateInfo)
                    }

                }
            }

        }


        srl.setOnRefreshListener {
            demoViewModel.refresh()
        }

        demoViewModel.refresh.observe(this, Observer {
            if (it == null) return@Observer
            srl.isRefreshing = it
        })


    }
}

class DemoViewModel : ViewModel() {
    val dataSource = DemoDataSource()

    val refresh = dataSource.refresh


    fun refresh() {
        dataSource.invalidate()
    }
}
