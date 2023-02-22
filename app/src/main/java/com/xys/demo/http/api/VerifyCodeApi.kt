package com.xys.demo.http.api

import com.hjq.http.config.IRequestApi
/**
 *    desc   : 验证码校验
 */
class VerifyCodeApi : IRequestApi {

    override fun getApi(): String {
        return "code/checkout"
    }

    /** 手机号 */
    private var phone: String? = null

    /** 验证码 */
    private var code: String? = null

    fun setPhone(phone: String?): VerifyCodeApi = apply {
        this.phone = phone
    }

    fun setCode(code: String?): VerifyCodeApi = apply {
        this.code = code
    }
}