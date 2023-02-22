package com.xys.demo.ui.fragment

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.xys.demo.aop.Permissions
import com.xys.demo.aop.SingleClick
import com.xys.demo.app.TitleBarFragment
import com.xys.demo.http.glide.GlideApp
import com.xys.demo.ui.activity.HomeActivity
import com.xys.demo.R

/**
 *    desc   : 消息 Fragment
 */
class MessageFragment : TitleBarFragment<HomeActivity>() {

    companion object {

        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }

    private val imageView: ImageView? by lazy { findViewById(R.id.iv_message_image) }

    override fun getLayoutId(): Int {
        return R.layout.message_fragment
    }

    override fun initView() {
        setOnClickListener(
            R.id.btn_message_image1, R.id.btn_message_image2, R.id.btn_message_image3,
            R.id.btn_message_toast, R.id.btn_message_permission, R.id.btn_message_setting,
            R.id.btn_message_black, R.id.btn_message_white, R.id.btn_message_tab
        )
    }

    override fun initData() {}

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    @SingleClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_message_image1 -> {

                imageView?.let {
                    it.visibility = View.VISIBLE
                    GlideApp.with(this)
                        .load("https://www.baidu.com/img/bd_logo.png")
                        .into(it)
                }
            }
            R.id.btn_message_image2 -> {

                imageView?.let {
                    it.visibility = View.VISIBLE
                    GlideApp.with(this)
                        .load("https://www.baidu.com/img/bd_logo.png")
                        .circleCrop()
                        .into(it)
                }
            }
            R.id.btn_message_image3 -> {

                imageView?.let {
                    it.visibility = View.VISIBLE
                    GlideApp.with(this)
                        .load("https://www.baidu.com/img/bd_logo.png")
                        .transform(RoundedCorners(resources.getDimension(R.dimen.dp_20).toInt()))
                        .into(it)
                }
            }
            R.id.btn_message_toast -> {

                toast("我是吐司")

            }
            R.id.btn_message_permission -> {

                requestPermission()
            }
            R.id.btn_message_setting -> {

                XXPermissions.startPermissionActivity(this)
            }
            R.id.btn_message_black -> {

                getStatusBarConfig()
                    .statusBarDarkFont(true)
                    .init()
            }
            R.id.btn_message_white -> {

                getStatusBarConfig()
                    .statusBarDarkFont(false)
                    .init()
            }
            R.id.btn_message_tab -> {

                HomeActivity.start(getAttachActivity()!!, HomeFragment::class.java)
            }
        }
    }

    @Permissions(Permission.CAMERA)
    private fun requestPermission() {
        toast("获取摄像头权限成功")
    }
}