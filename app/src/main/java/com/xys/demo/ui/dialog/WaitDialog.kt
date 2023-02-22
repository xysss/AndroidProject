package com.xys.demo.ui.dialog

import android.content.*
import android.view.*
import android.widget.TextView
import androidx.annotation.StringRes
import com.xys.base.BaseDialog
import com.xys.base.action.AnimAction
import com.xys.demo.R

/**
 *    desc   : 等待加载对话框
 */
class WaitDialog {

    class Builder(context: Context) : BaseDialog.Builder<Builder>(context) {

        private val messageView: TextView? by lazy { findViewById(R.id.tv_wait_message) }

        init {
            setContentView(R.layout.wait_dialog)
            setAnimStyle(AnimAction.ANIM_TOAST)
            setBackgroundDimEnabled(false)
            setCancelable(false)
        }

        fun setMessage(@StringRes id: Int): Builder = apply {
            setMessage(getString(id))
        }

        fun setMessage(text: CharSequence?): Builder = apply {
            messageView?.text = text
            messageView?.visibility = if (text == null) View.GONE else View.VISIBLE
        }
    }
}