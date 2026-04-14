package com.wsvita.biz.core.commons

import android.graphics.Color
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.MyLocationConfiguration
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

    override fun onStart() {
        super.onStart()
        if(needLocation()){
            if(mLocClient?.isStarted == false){
                mLocClient?.start();
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mLocClient?.stop();
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
        if(baiduScanSpan() > 0){
            //除了 LocationClientOption 本身，你的代码中还需要确保处理了以下两点，否则在 Android 12+ 上极易崩溃或无法定位
            //client.enableLocInForeground()
            //后期优化
        }
        //开始定位
        client.start();
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
        //声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(2000)
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当Gnss有效时按照1S1次频率输出Gnss结果
        locationOption.isLocationNotify = true
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启卫星定位
        locationOption.isOpenGnss = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        //locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        return locationOption;
    }

    companion object{
        private const val TAG = "WSVita_Bizcore_BizcoreActivity=>"
    }
}
