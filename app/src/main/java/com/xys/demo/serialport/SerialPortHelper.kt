package com.xys.demo.serialport

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.serial.port.kit.core.common.TypeConversion
import com.serial.port.manage.SerialPortManager
import com.serial.port.manage.data.WrapReceiverData
import com.serial.port.manage.data.WrapSendData
import com.serial.port.manage.listener.OnDataReceiverListener
import com.swallowsonny.convertextlibrary.*
import com.xys.demo.serialport.commond.SerialCommandProtocol
import com.xys.demo.serialport.model.SensorData
import com.xys.demo.serialport.model.SensorModel
import com.xys.demo.serialport.proxy.SerialPortProxy
import com.xys.demo.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.hgj.mvvmhelper.ext.logE

/**
 * 作者 : xys
 * 时间 : 2022-06-29 11:00
 * 描述 : 工具指令管理 只有使用的时候才会进行初始化
 */

object SerialPortHelper {
    private const val TAG = "SerialPortManager"
    private val mHandler = Handler(Looper.getMainLooper())
    private val mProxy = SerialPortProxy()
    private val transSendCodingList = ArrayList<Byte>()
    private lateinit var transSendCodingBytes: ByteArray

    //转码拼包
    private val transcodingBytesList = ArrayList<Byte>()
    private lateinit var afterBytes: ByteArray
    private val newLengthBytes = ByteArray(2)
    private var newLength = 0
    private var beforeIsFF = false
    private lateinit var recall: ReceiveDataCallBack
    private lateinit var senId: String
    private lateinit var senState: String
    private lateinit var sensorValue: String
    private lateinit var senOverFlow: String
    private lateinit var senDecimalLen: String
    private lateinit var senTempState: String
    private lateinit var senTempValue: String
    private lateinit var senHumidityState: String
    private lateinit var senHumidityValue: String
    private val sensorArray = ArrayList<SensorModel>()

    /**
     * 暴露SDK
     */
    val portManager: SerialPortManager
        get() = mProxy.portManager

    /**
     * 内部使用，默认开启串口
     */
    private val serialPortManager: SerialPortManager
        get() {
            // 默认开启串口
            if (!portManager.isOpenDevice) {
                portManager.open()
            }
            return portManager
        }


    /**
     * 读取设备版本信息
     *
     * @param listener 监听回调
     */
    fun readVersion() {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.onCmdReadVersionStatus())
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    val buffer: ByteArray = data.data
                    //analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun getSensorInfo() {
        val sends: ByteArray =transSendCoding(SerialCommandProtocol.getSensorInfoReq())
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 300, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    val buffer: ByteArray = data.data
                    //analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    private fun sendRecDeviceInfo() {
        val sends: ByteArray =transSendCoding(SerialCommandProtocol.getRecDDeviceInfoReq())
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    val buffer: ByteArray = data.data
                    //analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun sendNetState(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.sendNetStateReq(bytes))
        "发送网络状态：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(WrapSendData(sends, 3000, 300, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "发送网络状态 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun setDeviceSensorState(bytes: ByteArray) {
        val sends: ByteArray =transSendCoding(SerialCommandProtocol.setDeviceSensorDataReq(bytes))
        "停止主动上报：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 300, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    val buffer: ByteArray = data.data
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "停止主动上报 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun sendBeginUpdate(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.sendBeginUpdateReq(bytes))
        "开始固件更新请求：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 300, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "开始固件更新请求 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun sendUIReq(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.setUIReq(bytes))
        "发送开始UI更新请求 ${sends.size}: ${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(WrapSendData(sends, 3000, 5000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    "发送开始UI更新请求响应，success".logE(logFlag)
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "发送开始UI更新请求 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }


    private fun sendUIUpdate(bytes: ByteArray, allLength: Int, dataLength: Int) {
//        val byte= ByteArray(100)
//        System.arraycopy(bytes,0,byte,0,100)
        val allLengthByte= ByteArray(2)
        allLengthByte.writeInt16LE(allLength)
        val dataLengthByte= ByteArray(2)
        dataLengthByte.writeInt16LE(dataLength)

        val updateHeadByte=ByteArray(7)
        updateHeadByte[0]=0x55.toByte()
        updateHeadByte[1]=allLengthByte[1]
        updateHeadByte[2]=allLengthByte[0]
        updateHeadByte[3]=0x00.toByte()
        updateHeadByte[4]=0x0E.toByte()
        updateHeadByte[5]=dataLengthByte[1]
        updateHeadByte[6]=dataLengthByte[0]

        val sends: ByteArray = transSendCoding(SerialCommandProtocol.sendUIUpdateReq(updateHeadByte+bytes))
        //"发送分包UI更新请求 转码后长度：${sends.size} ：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 500, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "发送 分包固件更新请求onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    //"发送  分包固件更新请求 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }



    fun sendUpdate(bytes: ByteArray,allLength: Int,dataLength: Int) {
        val allLengthByte= ByteArray(2)
        allLengthByte.writeInt16LE(allLength)
        val dataLengthByte= ByteArray(2)
        dataLengthByte.writeInt16LE(dataLength)

        val updateHeadByte=ByteArray(7)
        updateHeadByte[0]=0x55.toByte()
        updateHeadByte[1]=allLengthByte[1]
        updateHeadByte[2]=allLengthByte[0]
        updateHeadByte[3]=0x00.toByte()
        updateHeadByte[4]=0x02.toByte()
        updateHeadByte[5]=dataLengthByte[1]
        updateHeadByte[6]=dataLengthByte[0]

        synchronized(this) {
            sendNum++
        }
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.sendUpdateReq(updateHeadByte+bytes))
        "分包固件更新请求 sendNum：$sendNum :${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "分包固件更新请求 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun sendEndUpdate(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.sendEndUpdateReq(bytes))
        "结束固件更新请求 Service：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "结束固件更新请求 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    private fun sendUIEndUpdate(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.sendUIEndUpdateReq(bytes))
        "发送 结束UI固件更新请求：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "发送 结束固件更新请求 onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "发送 结束固件更新请求 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun sendWeatherData(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.putWeatherData(bytes))
        "发送天气数据：${sends.toHexString()} : ${sends.toHexString(false).length}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    //analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "发送天气数据 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun sendTime(bytes: ByteArray) {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.putTime(bytes))

        "发送时间：${sends.toHexString()}".logE(logFlag)
        val isSuccess: Boolean = serialPortManager.send(WrapSendData(sends,3000, 500, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "发送时间 onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "发送时间 onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    fun getSensorData() {
        val sends: ByteArray = transSendCoding(SerialCommandProtocol.getSensorDaraReq())
        val isSuccess: Boolean = serialPortManager.send(
            WrapSendData(sends, 3000, 3000, 1),
            object : OnDataReceiverListener {
                override fun onSuccess(data: WrapReceiverData) {
                    //analyseMessage(data.data)
                }
                override fun onFailed(wrapSendData: WrapSendData, msg: String) {
                    "onFailed: $msg".logE(logFlag)
                }
                override fun onTimeOut() {
                    "onTimeOut: 发送数据或者接收数据超时".logE(logFlag)
                }
            })
        printLog(isSuccess, sends)
    }

    /**
     * 检测回调数据是否符合要求
     *
     * @param buffer 回调数据
     * @return true 符合要求 false 数据命令未通过校验
     */
    private fun checkCallData(buffer: ByteArray): Boolean {
        val tempData = TypeConversion.bytes2HexString(buffer)
        Log.i(TAG, "receive serialPort data ：$tempData")
        return buffer[0] == SerialCommandProtocol.baseStart[0] && SerialCommandProtocol.checkHex(
            buffer
        )
    }

    /**
     * 打印发送数据Log
     *
     * @param isSuccess 是否成功
     * @param bytes     数据
     */
    private fun printLog(isSuccess: Boolean, bytes: ByteArray) {
        val tempData = TypeConversion.bytes2HexString(bytes)
        "buildControllerProtocol:" + tempData + "，结果=" + if (isSuccess) "发送成功" else "发送失败".logE(logFlag)
    }

    /**
     * 切换到主线程
     *
     * @param runnable Runnable
     */
    private fun runOnUiThread(runnable: Runnable) {
        mHandler.post(runnable)
    }

    @Synchronized
    private fun sendUIUpdateFile(byteArray: ByteArray){
        scope.launch(Dispatchers.IO) {
            var mResultList = ByteArray(orderPackSize+4)
            if (byteArray.size>orderPackSize){
                var offsetIndex=0
                val mList=ByteArray(orderPackSize)
                var j=0
                for (i in 0 until byteArray.size){
                    if (i!=0 && i%orderPackSize==0){
                        val offSetByteArray= ByteArray(4)
                        offSetByteArray.writeInt32LE((i-orderPackSize).toLong())
                        val dataLength= ByteArray(2)
                        dataLength.writeInt16LE(orderPackSize)
                        mResultList=offSetByteArray+dataLength+mList

                        sendUIUpdate(mResultList, mResultList.size + 9, mResultList.size)

                        "UI分包： 总长度: ${byteArray.size} 发送进度： $i  ".logE(logFlag)
                        offsetIndex=i
                        while (!isRec8E01OK){
                            delay(10)
                            //"0x01 等待中 uiRecNum: $uiRecNum".logE(logFlag)
                        }
                        isRec8E01OK=false

                        while (uiRecNum == orderRecNum && !isRec8E05OK){
                            delay(10)
                            //"0x05等待中".logE(logFlag)
                        }
                        //isRec8E05OK=false
                        j=0
                        mList[j]=byteArray[i]
                        j++
                    }else{
                        mList[j]=byteArray[i]
                        j++
                    }
                }
                if (mList.isNotEmpty()){
                    val mLastList=ByteArray(j)
                    System.arraycopy(mList,0,mLastList,0,mLastList.size)
                    val offSetByteArray= ByteArray(4)
                    offSetByteArray.writeInt32LE(offsetIndex.toLong())
                    val dataLength= ByteArray(2)
                    dataLength.writeInt16LE(mLastList.size)
                    mResultList=offSetByteArray+dataLength+mLastList

                    sendUIUpdate(mResultList,mResultList.size+9,mResultList.size)
                    "发送 UI包 last 总长度: ${byteArray.size} 发送长度： ${mResultList.size} : ${mResultList.toHexString()}".logE(logFlag)
                }
            }else{
                val offSetByteArray= ByteArray(4)
                offSetByteArray.writeInt32LE(0.toLong())
                val dataLength= ByteArray(2)
                dataLength.writeInt16LE(byteArray.size)
                mResultList=offSetByteArray+dataLength+byteArray
                sendUIUpdate(mResultList,mResultList.size+9,mResultList.size)
                "长度不足$orderRecNum： last 总长度: ${byteArray.size} 发送长度： ${mResultList.size} : ${mResultList.toHexString()}".logE(logFlag)
            }
            //发送结束
            sendUIUpdateEnd(uIPackageByte)
        }
//        isNeedNewInit=true
//        baudRate=115200
//        val open = SerialPortHelper.portManager.open()
//        isNeedNewInit=false
//        delay(1000)
    }

    private fun sendUIUpdateEnd(byteArray: ByteArray){
        var checkSum=0L
        for (k in 0 until byteArray.size){
            checkSum+=byteArray[k].toInt() and 0xff
        }
        "UI end checkSum: $checkSum".logE(logFlag)
        val checkSumByte= ByteArray(4)
        checkSumByte.writeInt32LE(checkSum)
        sendUIEndUpdate(checkSumByte)
    }

    private  fun dealMsg8D(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 10) {
                if (it[7].toInt()==0){
                    "开始UI更新请求响应，成功".logE(logFlag)
                    sendUIUpdateFile(uIPackageByte)
                }
                else if (it[7].toInt()==1){
                    "开始UI更新请求响应，失败".logE(logFlag)
                }else if (it[7].toInt()==2){
                    "开始UI更新请求响应，等通知".logE(logFlag)
                }
            }
        }
    }

    private fun dealMsg8E(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 10) {
                if (it[7].toInt()==1){
                    isRec8E01OK=true
                    uiRecNum++
                    //"发送UI映像文件请求响应，成功 uiRecNum: $uiRecNum".logE(logFlag)
                }else if (it[7].toInt()==5){
                    isRec8E05OK=true
                    uiRecNum=0
                }
                else{
                    isRec8E01OK=false
                    isRec8E05OK=false
                }
            }
        }
    }

    private fun dealMsgC0(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 33) {
                //版本号
                //mmkv.putString(ValueKey.deviceHardwareVersion, it[7].toInt().toString() + "." + it[8].toInt().toString())
                //mmkv.putString(ValueKey.deviceSoftwareVersion, it[9].toInt().toString() + "." + it[10].toInt().toString())
                //设备序列号
                var i = 11
                while (i < it.size)
                    if (it[i] == ByteUtils.FRAME_00) break else i++
                val tempBytes: ByteArray = it.readByteArrayBE(11, i - 11)
                //mmkv.putString(ValueKey.deviceId, String(tempBytes))
                "设备信息响应成功: ${String(tempBytes)}".logE("xysLog")
                sendRecDeviceInfo()
                recall.initLocation()
            }
        }
    }

    private fun dealMsg82(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 10) {
                if (it[7].toInt()==0){
                    synchronized(this) {
                        sendNum--
                    }
                    "发送映像文件请求,成功 sendNum: $sendNum".logE(logFlag)
                }
                else if (it[7].toInt()==1){
                    "发送映像文件请求,失败 sendNum:$sendNum".logE(logFlag)
                }
            }
        }
    }

    private fun dealMsgC3(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 9) {
                "收到C3成功".logE(logFlag)
//                baudRate=921600
//                isNeedNewInit=true
//                val open = SerialPortHelper.portManager.open()
//                "串口打开${if (open) "成功" else "失败"}".logE(logFlag)
//                isNeedNewInit=false
//                sendUIUpdateFile(uIPackageByte)
            }else{
                "收到C3错误".logE(logFlag)
            }
        }
    }

    private fun dealMsg8C(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 10) {
                if (it[7].toInt()==0){
                    "发送安卓wifi状态请求响应,成功".logE(logFlag)
                }
                else {
                    "发送安卓wifi状态请求响应,失败".logE(logFlag)
                }
            }
        }
    }

    private fun dealMsg8F(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 9) {
                "发送UI映像文件结束响应，成功".logE(logFlag)
            }
        }
    }
    private fun dealMsg81(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 10) {
                if (it[7].toInt()==0){
                    "开始固件更新请求,失败".logE(logFlag)
                }
                else if (it[7].toInt()==1){
                    "开始固件更新请求 自动模式,成功".logE(logFlag)
                    sendFirmwareUpdateFile(firmwarePackageByte)
                }
                else if (it[7].toInt()==2){
                    "等待STM的通知信息，手动模式，是否继续更新".logE(logFlag)
                }
            }
        }
    }

    private fun sendFirmwareUpdateFile(byteArray: ByteArray){
        scope.launch(Dispatchers.IO) {
            var mResultList=ByteArray(518)
            if (byteArray.size>512){
                var offsetIndex=0
                val mList=ByteArray(512)
                var j=0
                for (i in byteArray.indices){
                    if (i!=0 && i%512==0){
                        if (i==1024){
                            delay(500)
                        }else{
                            delay(100)
                        }
                        val offSetByteArray= ByteArray(4)
                        offSetByteArray.writeInt32LE((i-512).toLong())
                        val dataLength= ByteArray(2)
                        dataLength.writeInt16LE(512)
                        mResultList=offSetByteArray+dataLength+mList
                        sendUpdate(mResultList,mResultList.size+9,mResultList.size)
                        //"update分包： 总长度: ${byteArray.size} 发送进度： $i  长度：: ${mResultList.toHexString()}}".logE(logFlag)
                        j=0
                        offsetIndex=i
                        mList[j]=byteArray[i]
                        j++
                    }else{
                        mList[j]=byteArray[i]
                        j++
                    }
                }
                if (mList.isNotEmpty()){
                    val mLastList=ByteArray(j)
                    System.arraycopy(mList,0,mLastList,0,mLastList.size)
                    val offSetByteArray= ByteArray(4)
                    offSetByteArray.writeInt32LE((offsetIndex).toLong())
                    val dataLength= ByteArray(2)
                    dataLength.writeInt16LE(mLastList.size)
                    mResultList=offSetByteArray+dataLength+mLastList
                    sendUpdate(mResultList,mResultList.size+9,mResultList.size)
                    "update last 总长度: ${byteArray.size} 发送长度： ${mResultList.size} : ${mResultList.toHexString()}".logE(logFlag)
                }
            }else{
                val offSetByteArray= ByteArray(4)
                offSetByteArray.writeInt32LE(0.toLong())
                val dataLength= ByteArray(2)
                dataLength.writeInt16LE(byteArray.size)
                mResultList=offSetByteArray+dataLength+byteArray

                sendUpdate(mResultList,mResultList.size+9,mResultList.size)
                "update不足512： last 总长度: ${byteArray.size} 发送长度： ${mResultList.size} : ${mResultList.toHexString()}".logE(logFlag)
            }
            var checkSum=0L
            for (k in byteArray.indices){
                checkSum+=byteArray[k].toInt() and 0xff
            }
            "checkSum: $checkSum".logE(logFlag)
            val checkSumByte= ByteArray(4)
            checkSumByte.writeInt32LE(checkSum)
            sendEndUpdate(checkSumByte)
        }
    }

    private fun dealMsg88(mBytes: ByteArray) {
        mBytes.let {
            if (it.size > 10) {
                val sensorNum = it.readByteArrayBE(7 + 0, 2).readInt16LE()
                for (i in 0 until sensorNum) {
                    val sensorId = it[7 + 2 + i * 37].toInt().toString()
                    val sensorType = it.readByteArrayBE(7 + 3 + i * 37, 2).readInt16LE().toString()
                    val sensorVersion = it.readByteArrayBE(7 + 5 + i * 37, 2).readInt16LE().toString()
                    var k = 14
                    while (k < it.size)
                        if (it[k] == ByteUtils.FRAME_00) break else k++
                    val tempBytes: ByteArray = it.readByteArrayBE(14, k - 14)
                    //val name = tempBytes.toAsciiString()
                    val sensorName = String(tempBytes)
                    val sensorUnit: String = when (it[7 + 27 + i * 37].toInt()) {
                        0 -> "PPM"
                        1 -> "vol%"
                        2 -> "LEL%"
                        3 -> "mg/m3"
                        4 -> "PPB"
                        else -> ""
                    }
                    val sensorReserv = it[7 + 28 + i * 37].toInt().toString()
                    val sensorWm = it.readByteArrayBE(7 + 29 + i * 37, 2).readInt16LE().toString()
                    val sensorFullScale = it.readByteArrayBE(7 + 31 + i * 37, 4).readInt32LE().toString()
                    val sensorSensibility = it.readByteArrayBE(7 + 35 + i * 37, 4).readFloatLE().toInt().toString()
                    val sensorModel = SensorModel(
                        sensorId,
                        sensorType,
                        sensorVersion,
                        sensorName,
                        sensorUnit,
                        sensorReserv,
                        sensorWm,
                        sensorFullScale,
                        sensorSensibility
                    )
                    sensorModel.toString().logE(logFlag)
                    sensorArray.add(sensorModel)
                }
            }
        }
    }

    private fun dealMsg84(mBytes: ByteArray) {
        mBytes.let {
            if (it.size > 25) {
                if (it[7] == ByteUtils.Msg26) {
                    senId = it[10].toInt().toString()
                    senState = it[11].toInt().toString()  //0-无故障，1-故障
                    sensorValue = it.readByteArrayBE(12, 2).readInt16LE().toString()
                    senOverFlow = it[14].toInt().toString()  //0-未溢出，1-溢出
                    senDecimalLen = it[15].toInt().toString()  ////小数点后位数
                }
                if (it[16] == ByteUtils.Msg61) {
                    senTempState = it[19].toInt().toString()  //0-无故障，1-故障
                    senTempValue = it[20].toInt().toString()  //温度，有符号单字节整数，单位：摄氏度
                }
                if (it[21] == ByteUtils.Msg62) {
                    senHumidityState = it[24].toInt().toString()  //0-无故障，1-故障
                    senHumidityValue = it[25].toInt().toString()  //湿度，无符号单字节整数，单位：%
                }
                val sensorData = SensorData(
                    senId,
                    senState,
                    sensorValue,
                    senOverFlow,
                    senDecimalLen,
                    senTempState,
                    senTempValue,
                    senHumidityState,
                    senHumidityValue
                )
                sensorData.toString().logE(logFlag)
            }
        }
    }

    private fun dealMsg87(mBytes: ByteArray){
        mBytes.let {
            if (it.size == 10) {
                if (it[7].toInt()==0){
                    "发送时间响应,成功".logE(logFlag)
                }else {
                    "发送时间响应,失败".logE(logFlag)
                }
            }
        }
    }

    private fun dealMsgC2(mBytes: ByteArray) {
        mBytes.let {
            if (it.size == 9) {
                "收到安卓STM确认更新固件请求".logE(logFlag)
                sendFirmwareUpdateFile(firmwarePackageByte)
            }
        }
    }

    //协议分发
    fun analyseMessage(mBytes: ByteArray?) {
        mBytes?.let {
            when (it[4]) {
                //设备信息
                ByteUtils.MsgC0 -> {
                    dealMsgC0(it)
                }
                //传感器信息读取请求
                ByteUtils.Msg88 -> {
                    //dealMsg88(it)
                }
                //设置时间响应
                ByteUtils.Msg87 -> {
                    dealMsg87(it)
                }
                ByteUtils.Msg84 -> {
                    //dealMsg84(it)
                }
                ByteUtils.Msg81 -> {
                    dealMsg81(it)
                }
                ByteUtils.MsgC2 -> {
                    dealMsgC2(it)
                }
                ByteUtils.Msg82 -> {
                    dealMsg82(it)
                }
                ByteUtils.Msg8D -> {
                    dealMsg8D(it)
                }
                ByteUtils.Msg8C -> {
                    dealMsg8C(it)
                }
                ByteUtils.MsgC3 -> {
                    dealMsgC3(it)
                }
                ByteUtils.Msg8E -> {
                    dealMsg8E(it)
                }
                ByteUtils.Msg8F -> {
                    dealMsg8F(it)
                }
                else->{}
            }
        }
    }

    //拼包函数
    fun getResolvedByteArray(tempBuffer : ByteArray,size : Int): ByteArray?{
        for (i in 0..size){
            tempBuffer[i].let {
                if (it == ByteUtils.FRAME_START) {
                    transcodingBytesList.clear()
                    transcodingBytesList.add(it)
                } else if (beforeIsFF) {
                    when (it) {
                        ByteUtils.FRAME_FF -> {
                            transcodingBytesList.add(ByteUtils.FRAME_FF)
                        }
                        ByteUtils.FRAME_00 -> {
                            transcodingBytesList.add(ByteUtils.FRAME_START)
                        }
                        else -> {
                            transcodingBytesList.add(ByteUtils.FRAME_FF)
                            transcodingBytesList.add(it)
                        }
                    }
                    beforeIsFF = false
                } else if (!beforeIsFF) {
                    if (it == ByteUtils.FRAME_FF) {
                        beforeIsFF = true
                    } else {
                        beforeIsFF = false
                        transcodingBytesList.add(it)
                    }
                }
                //取协议数据长度
                if (transcodingBytesList.size == 3) {
                    newLengthBytes[0] = transcodingBytesList[1]
                    newLengthBytes[1] = transcodingBytesList[2]
                    newLength = newLengthBytes.readInt16BE()
                    //"自定义协议长度: $newLength".logE("协议长度")
                }
                if (transcodingBytesList.size == newLength && transcodingBytesList.size >= 9) {
                    transcodingBytesList.let { arrayList ->
                        afterBytes = ByteArray(arrayList.size)
                        for (k in afterBytes.indices) {
                            afterBytes[k] = arrayList[k]
                        }
                    }
                    if (afterBytes[0] == ByteUtils.FRAME_START && afterBytes[afterBytes.size - 1] == ByteUtils.FRAME_END) {
                        //CRC校验
                        if (Crc8.isFrameValid(afterBytes, afterBytes.size)) {
                            isRecOK = true
                            return afterBytes  //分发数据
                            //"协议正确: ${afterBytes.toHexString()}".logE("xysLog")
                        } else {
                            "CRC校验错误，协议长度: $newLength : ${afterBytes.toHexString()}".logE("xysLog")
                            isRecOK = false
                        }
                    } else {
                        "协议开头结尾不对:  ${afterBytes.toHexString()}".logE("xysLog")
                        isRecOK = false
                    }
                    transcodingBytesList.clear()
                }else if (newLength < 9 && transcodingBytesList.size > 9) { //协议长度不够
                    "解析协议不完整，协议长度: $newLength  解析长度：${transcodingBytesList.size} ,${transcodingBytesList.toHexString()}".logE("xysLog")
                    isRecOK = false
                    //BleHelper.retryHistoryMessage(recordCommand,alarmCommand)
                    transcodingBytesList.clear()
                }
            }
        }
        return null
    }

    //转码函数
    @Synchronized
    private fun transSendCoding(bytes: ByteArray): ByteArray {
        //"转码前长度：${bytes.size} ：${bytes.toHexString()}".logE(logFlag)
        bytes.let {
            var i = 1
            if (it[0] == ByteUtils.FRAME_START) {
                transSendCodingList.clear()
                transSendCodingList.add(it[0])
            }
            while (i < it.size) {
                //校验开头
                //开始转码
                when {
                    it[i] == ByteUtils.FRAME_START -> {
                        transSendCodingList.add(ByteUtils.FRAME_FF)
                        transSendCodingList.add(ByteUtils.FRAME_00)
                    }
                    it[i] == ByteUtils.FRAME_FF -> {
                        transSendCodingList.add(ByteUtils.FRAME_FF)
                        transSendCodingList.add(ByteUtils.FRAME_FF)
                    }
                    else -> transSendCodingList.add(it[i])
                }
                i++
            }
        }

        transSendCodingList.let {
            if (it.size>0){
                transSendCodingBytes = ByteArray(it.size)
                for (k in 0 until transSendCodingBytes.size) {
                    transSendCodingBytes[k] = it[k]
                }
            }
        }
        return transSendCodingBytes
    }

    fun setUiCallback(dataCallback: ReceiveDataCallBack) {
        this.recall = dataCallback
    }

    interface ReceiveDataCallBack {
        fun initLocation()
    }


}