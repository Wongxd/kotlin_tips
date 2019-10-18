package io.wongxd.ktolin_tips_demo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.wongxd.kotlin_tips.startAty
import io.wongxd.kt_dsl.rv.render.linear
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val demoViewModel by lazy { ViewModelProviders.of(this)[DemoViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
