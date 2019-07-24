package io.wongxd.kotlin_tips.simple_for_result

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * Created by wongxd on 2018/12/24.
 * https://github.com/wongxd
 * wxd1@live.com
 */
object SimpleOnActivityResult {

    private const val TAG = "SimpleOnActivityResult"
    private var mSimpleOnResultFragment: SimpleOnActivityResultAsset.SimpleOnResultFragment? = null

    fun simpleForResult(activity: AppCompatActivity?): SimpleOnActivityResult {
        mSimpleOnResultFragment =
            getOnResultFragment(activity?.supportFragmentManager)
        return this
    }

    fun simpleForResult(fragment: Fragment?): SimpleOnActivityResult {
        mSimpleOnResultFragment =
            getOnResultFragment(fragment?.childFragmentManager)
        return this
    }

    private fun getOnResultFragment(fragmentManager: FragmentManager?): SimpleOnActivityResultAsset.SimpleOnResultFragment {
        var simpleOnResultFragment: SimpleOnActivityResultAsset.SimpleOnResultFragment? =
            findSimpleOnResultFragment(fragmentManager)
        if (simpleOnResultFragment == null) {
            simpleOnResultFragment =
                SimpleOnActivityResultAsset.SimpleOnResultFragment()
            fragmentManager
                ?.beginTransaction()
                ?.add(simpleOnResultFragment, TAG)
                ?.commitAllowingStateLoss()
            fragmentManager?.executePendingTransactions()
        }
        return simpleOnResultFragment
    }

    private fun findSimpleOnResultFragment(fragmentManager: FragmentManager?): SimpleOnActivityResultAsset.SimpleOnResultFragment? {
        return fragmentManager?.findFragmentByTag(TAG) as SimpleOnActivityResultAsset.SimpleOnResultFragment?
    }


    internal fun startForResult(intent: Intent, callback: SimpleOnActivityResultCallback) {
        mSimpleOnResultFragment?.startForResult(intent, callback)
    }

    private fun startForResult(clazz: Class<*>, callback: SimpleOnActivityResultCallback) {
        startForResult(
            Intent(
                mSimpleOnResultFragment?.activity,
                clazz
            ), callback
        )
    }


}

class SimpleOnActivityResultAsset {

    /**
     *
     * @Description: 真正调用 startActivity 和处理 onActivityResult 的类。
     */
    class SimpleOnResultFragment : Fragment() {

        private val mCallbacks: MutableMap<Int, SimpleOnActivityResultCallback> = mutableMapOf()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }


        fun startForResult(intent: Intent, callback: SimpleOnActivityResultCallback) {
            val requestCode = generateRequestCode()
            mCallbacks[requestCode] = callback
            startActivityForResult(intent, requestCode)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            //callback方式的处理
            val callback = mCallbacks.remove(requestCode)
            callback?.invoke(requestCode, resultCode, data)
        }

        private fun generateRequestCode(): Int {
            val random = Random()
            while (true) {
                val code = random.nextInt(65536)
                if (!mCallbacks.containsKey(code)) {
                    return code
                }
            }
        }
    }
}

/**
 *
 * requestCode: Int, resultCode: Int, data: Intent?
 *
 */
private typealias  SimpleOnActivityResultCallback = (Int, Int, Intent?) -> Unit