package com.xys.demo.ui.activity

import android.app.Activity
import android.content.*
import android.view.*
import android.view.animation.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.hjq.toast.ToastUtils
import com.xys.base.BaseDialog
import com.xys.demo.aop.Log
import com.xys.demo.aop.SingleClick
import com.xys.demo.app.AppActivity
import com.xys.demo.http.api.GetCodeApi
import com.xys.demo.http.api.PhoneApi
import com.xys.demo.http.model.HttpData
import com.xys.demo.manager.InputTextManager
import com.xys.demo.ui.dialog.TipsDialog
import com.xys.widget.view.CountdownView
import com.xys.demo.R

/**
 *    desc   : 设置手机号
 */
class PhoneResetActivity : AppActivity(), OnEditorActionListener {

    companion object {

        private const val INTENT_KEY_IN_CODE: String = "code"

        @Log
        fun start(context: Context, code: String?) {
            val intent = Intent(context, PhoneResetActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_CODE, code)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val phoneView: EditText? by lazy { findViewById(R.id.et_phone_reset_phone) }
    private val codeView: EditText? by lazy { findViewById(R.id.et_phone_reset_code) }
    private val countdownView: CountdownView? by lazy { findViewById(R.id.cv_phone_reset_countdown) }
    private val commitView: Button? by lazy { findViewById(R.id.btn_phone_reset_commit) }

    /** 验证码 */
    private var verifyCode: String? = null

    override fun getLayoutId(): Int {
        return R.layout.phone_reset_activity
    }

    override fun initView() {
        setOnClickListener(countdownView, commitView)
        codeView?.setOnEditorActionListener(this)
        commitView?.let {
            InputTextManager.with(this)
                .addView(phoneView)
                .addView(codeView)
                .setMain(it)
                .build()
        }
    }

    override fun initData() {
        verifyCode = getString(INTENT_KEY_IN_CODE)
    }

    @SingleClick
    override fun onClick(view: View) {
        if (view === countdownView) {
            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }
            if (true) {
                toast(R.string.common_code_send_hint)
                countdownView?.start()
                return
            }

            // 获取验证码
            EasyHttp.post(this)
                .api(GetCodeApi().apply {
                    setPhone(phoneView?.text.toString())
                })
                .request(object : HttpCallback<HttpData<Void?>>(this) {
                    override fun onSucceed(data: HttpData<Void?>) {
                        toast(R.string.common_code_send_hint)
                        countdownView?.start()
                    }
                })

        } else if (view === commitView) {

            if (phoneView?.text.toString().length != 11) {
                phoneView?.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim))
                toast(R.string.common_phone_input_error)
                return
            }
            if (codeView?.text.toString().length != resources.getInteger(R.integer.sms_code_length)) {
                ToastUtils.show(R.string.common_code_error_hint)
                return
            }

            // 隐藏软键盘
            hideKeyboard(currentFocus)
            if (true) {
                TipsDialog.Builder(this)
                    .setIcon(TipsDialog.ICON_FINISH)
                    .setMessage(R.string.phone_reset_commit_succeed)
                    .setDuration(2000)
                    .addOnDismissListener(object : BaseDialog.OnDismissListener {

                        override fun onDismiss(dialog: BaseDialog?) {
                            finish()
                        }
                    })
                    .show()
                return
            }

            // 更换手机号
            EasyHttp.post(this)
                .api(PhoneApi().apply {
                    setPreCode(verifyCode)
                    setPhone(phoneView?.text.toString())
                    setCode(codeView?.text.toString())
                })
                .request(object : HttpCallback<HttpData<Void?>>(this) {

                    override fun onSucceed(data: HttpData<Void?>) {
                        TipsDialog.Builder(this@PhoneResetActivity)
                            .setIcon(TipsDialog.ICON_FINISH)
                            .setMessage(R.string.phone_reset_commit_succeed)
                            .setDuration(2000)
                            .addOnDismissListener(object : BaseDialog.OnDismissListener {

                                override fun onDismiss(dialog: BaseDialog?) {
                                    finish()
                                }
                            })
                            .show()
                    }
                })
        }
    }

    /**
     * [TextView.OnEditorActionListener]
     */
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // 模拟点击提交按钮
            commitView?.let {
                if (it.isEnabled) {
                    onClick(it)
                    return true
                }
            }
        }
        return false
    }
}