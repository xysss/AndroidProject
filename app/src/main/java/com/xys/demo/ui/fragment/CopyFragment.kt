package com.xys.demo.ui.fragment

import com.xys.demo.app.AppFragment
import com.xys.demo.ui.activity.CopyActivity
import com.xys.demo.R

/**
 *    desc   : 可进行拷贝的副本
 */
class CopyFragment : AppFragment<CopyActivity>() {

    companion object {

        fun newInstance(): CopyFragment {
            return CopyFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.copy_fragment
    }

    override fun initView() {}

    override fun initData() {}
}