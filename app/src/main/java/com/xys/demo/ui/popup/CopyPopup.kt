package com.xys.demo.ui.popup

import android.content.Context
import com.xys.base.BasePopupWindow
import com.xys.demo.R


/**
 *    desc   : 可进行拷贝的副本
 */
class CopyPopup {

    class Builder(context: Context) : BasePopupWindow.Builder<Builder>(context) {

        init {
            setContentView(R.layout.copy_popup)
        }
    }
}