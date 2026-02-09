package com.wsvita.biz.core.commons

import android.graphics.Color
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.wsvita.biz.core.configure.BizcoreConfig
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.biz.core.location.SDKLocationListener
import com.wsvita.core.common.AppActivity
import com.wsvita.core.configure.ScreenConfig
import com.wsvita.framework.utils.SLog
import ext.JsonExt.toJson

abstract class BizcoreActivity<D : ViewDataBinding,V : BizcoreViewModel> : AppActivity<D, V>(){
    private var mLocClient: LocationClient? = null
    private var sdkLocationListener : SDKLocationListener? = null;

    override fun initScreenConfig(): ScreenConfig {
        val c = ScreenConfig();
        c.isFullScreen = false;
        c.statusBarColor = Color.BLACK;
        c.lightIcons = false;
        return c;
    }

    override fun addObserve() {
        super.addObserve()
        viewModel.config.observe(this, Observer {
            onConfigChanged(it);
        })

        viewModel.location.observe(this, Observer {
            onLocationChanged(it);
        })
    }

    protected open fun onConfigChanged(config : BizcoreConfig){
        SLog.d(TAG,"onConfigChanged");
    }

    override fun performLocationAction() {
        super.performLocationAction()
        // 1. 业务开关检查
        if (!needLocation()) {
            SLog.i(TAG, "Location task ignored: needLocation() is false.")
            return
        }
        SLog.w(TAG, "Location permission status confirmed. Executing...")

        // 2. 初始化定位客户端 (单例检查，避免重复创建)
        if (mLocClient == null) {
            // 注意：百度 SDK 建议传递 ApplicationContext
            mLocClient = LocationClient(applicationContext)
        }
        val client = mLocClient ?: return
        sdkLocationListener = SDKLocationListener();
        sdkLocationListener?.onLocation { bdLocation, location ->
            SLog.d(TAG,"sdkLocationListener,onLocation");
            viewModel.receiveLocation(location);
        }
        client.registerLocationListener(sdkLocationListener)
        // 3. 配置参数 (调用私有方法)
        client.locOption = initLocationOption()
    }

    protected open fun onLocationChanged(location : BizLocation){
        SLog.d(TAG,"onLocationChanged,time:${systemTime()}");
        SLog.i(TAG,"onLocationChanged,location:${location.toJson()}}");
    }

    /**
     * ╔══════════════════════════════════════════════════════════════════════════════════╗
     * ║ 私有实现                                                                          ║
     * ╚══════════════════════════════════════════════════════════════════════════════════╝
     */

    /**
     * 初始化百度定位
     */
    private fun initLocationOption(): LocationClientOption {
        val option = LocationClientOption()

        // 显式赋值，禁止使用 apply 闭包
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy // 高精度模式
        option.coorType = "bd09ll"         // 百度经纬度坐标系 (GCJ02/BD09LL)
        option.setScanSpan(0)              // 0表示单次定位
        option.isOpenGps = true            // 允许使用GPS
        option.setIsNeedAddress(true)      // 需要详细地址描述

        return option
    }

    companion object{
        private const val TAG = "WSVita_Bizcore_BizcoreActivity=>"
    }
}
