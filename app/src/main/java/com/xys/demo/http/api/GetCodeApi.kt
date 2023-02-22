package com.xys.demo.http.api

import com.hjq.http.config.IRequestApi


/**
 *    desc   : 获取验证码
 */
class GetCodeApi : IRequestApi {

    override fun getApi(): String {
        return "code/get"
    }

    /** 手机号 */
    private var phone: String? = null

    fun setPhone(phone: String?): GetCodeApi = apply {
        this.phone = phone
    }
}