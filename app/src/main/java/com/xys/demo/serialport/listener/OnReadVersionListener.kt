package com.xys.demo.serialport.listener

import com.xys.demo.serialport.model.DeviceVersionModel


/**
 * 作者 : xys
 * 时间 : 2022-06-29 11:04
 * 描述 : 读取版本
 */
interface OnReadVersionListener {
    /**
     * 结果
     *
     * @param deviceVersionModel 设备信息
     */
    fun onResult(deviceVersionModel: DeviceVersionModel)
}