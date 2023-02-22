package com.xys.demo.http.api

import com.hjq.http.config.IRequestApi
import java.io.File

/**
 *    desc   : 上传图片
 */
class UpdateImageApi : IRequestApi {

    override fun getApi(): String {
        return "update/image"
    }

    /** 图片文件 */
    private var image: File? = null

    fun setImage(image: File?): UpdateImageApi = apply {
        this.image = image
    }
}