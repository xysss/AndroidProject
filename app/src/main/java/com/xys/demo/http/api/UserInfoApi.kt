package com.xys.demo.http.api

import com.hjq.http.config.IRequestApi


/**
 *    desc   : 获取用户信息
 */
class UserInfoApi : IRequestApi {

    override fun getApi(): String {
        return "user/info"
    }

    class Bean {

    }
}