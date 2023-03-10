package com.xys.demo.serialport

import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnAddressCheckCall
import com.serial.port.manage.listener.OnDataCheckCall
import java.io.InputStream


/**
 * 作者 : xys
 * 时间 : 2022-06-29 11:14
 * 描述 : 数据工具类
 */

object DataConvertUtil {
    /**
     * 检测命令所属指令
     *
     * @param command 命令字段
     * @param buffer  回调数据(取第二个参数命令)
     * @return true 符合 false 不符合
     */
    private fun checkCommand(command: ByteArray, buffer: ByteArray): Boolean {
        return command[1] == buffer[1]
    }

    /**
     * 自定义通讯协议
     *
     * @return 协议回调
     */
    fun customProtocol(): OnDataCheckCall {
        return object : OnDataCheckCall {
            override fun customCheck(
                inputStream: InputStream, onDataPickCall: (WrapReceiverData) -> Unit
            ): Boolean {
                val tempBuffer = ByteArray(64)
                val bodySize = inputStream.read(tempBuffer)

                val resolvedTBuffer = SerialPortHelper.getResolvedByteArray(tempBuffer,bodySize)

                return if (resolvedTBuffer!=null) {
                    onDataPickCall.invoke(WrapReceiverData(resolvedTBuffer, resolvedTBuffer.size))
                    true
                } else {
                    false
                }
            }
        }
    }

    /**
     * 校验地址
     *
     * @return 地址回调
     */
    fun addressCheckCall(): OnAddressCheckCall {
        return object : OnAddressCheckCall {
            override fun checkAddress(
                wrapSendData: WrapSendData,
                wrapReceiverData: WrapReceiverData
            ): Boolean {
                return checkCommand(wrapSendData.sendData, wrapReceiverData.data)
            }
        }
    }

}