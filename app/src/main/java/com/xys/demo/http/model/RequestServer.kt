package com.xys.demo.http.model

import com.hjq.http.config.IRequestServer
import com.hjq.http.model.BodyType
import com.xys.demo.other.AppConfig

/**
 *    desc   : 服务器配置
 */
class RequestServer : IRequestServer {

    override fun getHost(): String {
        return AppConfig.getHostUrl()
    }

    override fun getPath(): String {
        return "api/"
    }

    override fun getType(): BodyType {
        // 以表单的形式提交参数
        return BodyType.FORM
    }
}