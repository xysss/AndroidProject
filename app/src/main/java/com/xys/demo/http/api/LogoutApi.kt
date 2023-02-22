package com.xys.demo.http.api

import com.hjq.http.config.IRequestApi

/**
 *    desc   : 退出登录
 */
class LogoutApi : IRequestApi {

    override fun getApi(): String {
        return "user/logout"
    }
}