package com.xys.demo.ui.dialog

import android.content.Context
import android.view.Gravity
import com.xys.base.BaseDialog
import com.xys.base.action.AnimAction
import com.xys.demo.R

/**
 *    desc   : 可进行拷贝的副本
 */
class CopyDialog {

    class Builder(context: Context) : BaseDialog.Builder<Builder>(context) {

        init {
            setContentView(R.layout.copy_dialog)
            setAnimStyle(AnimAction.ANIM_BOTTOM)
            setGravity(Gravity.BOTTOM)
        }
    }
}