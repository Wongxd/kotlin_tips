package io.wongxd.kt_dsl.rv.render

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import io.wongxd.kt_dsl.rv.prepare_data.FetchingState


/**
 *根据手机的分辨率从 dp 的单位 转成为 px(像素)
 *
 */
val Int.dp2px: Int
    get() = (0.5f + this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
val Int.px2dp: Int
    get() = (this / Resources.getSystem().getDisplayMetrics().density).toInt()


class RvRenderStateView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var tvState: TextView? = null
    private var stateLoading: ProgressBar? = null

    init {


        val pb = ProgressBar(context)
        val lp: FrameLayout.LayoutParams = pb.layoutParams as LayoutParams?
                ?: FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.width = 40.dp2px
        lp.height = 40.dp2px
        lp.gravity = Gravity.CENTER
        stateLoading = pb
        addView(pb, lp)

        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        val tvLp: FrameLayout.LayoutParams = pb.layoutParams as LayoutParams?
                ?: FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        tvLp.height = 40.dp2px
        tvLp.width = FrameLayout.LayoutParams.MATCH_PARENT
        tvLp.gravity = Gravity.CENTER
        tvState = tv
        addView(tv, tvLp)


        stateLoading?.visibility = View.VISIBLE
        tvState?.visibility = View.GONE
    }


    fun setState(newState: RvRenderStateItem) {
        tvState?.setOnClickListener { }

        tvState?.text = newState.stateDes

        when {
            newState.state == FetchingState.FETCHING -> {
                stateLoading?.visibility = View.VISIBLE
                tvState?.visibility = View.GONE
            }
            newState.state == FetchingState.FETCHING_ERROR -> {
                stateLoading?.visibility = View.GONE
                tvState?.visibility = View.VISIBLE
                tvState?.setOnClickListener { newState.retry() }
            }
            newState.state == FetchingState.DONE_FETCHING -> {
                stateLoading?.visibility = View.GONE
                tvState?.visibility = View.GONE
            }
            newState.state == FetchingState.NO_MORE_DATA -> {
                stateLoading?.visibility = View.GONE
                tvState?.visibility = View.VISIBLE
            }
            else -> {
                stateLoading?.visibility = View.GONE
                tvState?.visibility = View.GONE
            }
        }
    }
}