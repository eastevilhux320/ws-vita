package com.wsvita.biz.core.location

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.wsvita.biz.core.entity.BizLocation
import com.wsvita.framework.utils.SLog

/**
 * ### 百度地图 SDK 定位监听器封装
 * * **设计意图**：将 SDK 原始数据转换为业务实体 [BizLocation]，并通过弱引用回调避免内存泄露。
 * * **规范约束**：禁止使用主构造函数，显式展示构造方法。
 */
class SDKLocationListener : BDAbstractLocationListener() {

    companion object{
        private const val TAG = "WSV_SDK_LocationListener=>"
    }

    private var onLocation : ((bdLocation : BDLocation?,location:BizLocation)->Unit)? = null;

    fun onLocation(onLocation : ((bdLocation : BDLocation?,location:BizLocation)->Unit)){
        this.onLocation = onLocation;
    }

    override fun onReceiveLocation(p0: BDLocation?) {
        SLog.d(TAG,"onReceiveLocation");
        p0?.let {bdLocation->
            val bizLoc = BizLocation()
            bizLoc.address = bdLocation.addrStr
            bizLoc.country = bdLocation.country
            bizLoc.province = bdLocation.province
            bizLoc.city = bdLocation.city
            bizLoc.district = bdLocation.district
            bizLoc.longitude = bdLocation.longitude.toString()
            bizLoc.latitude = bdLocation.latitude.toString()
            onLocation?.invoke(bdLocation,bizLoc);
        }
    }

    override fun onConnectHotSpotMessage(p0: String?, p1: Int) {
        super.onConnectHotSpotMessage(p0, p1)
        SLog.d(TAG,"onConnectHotSpotMessage,code:${p1},msg:${p0}");
    }

    override fun onLocDiagnosticMessage(p0: Int, p1: Int, p2: String?) {
        super.onLocDiagnosticMessage(p0, p1, p2)
        SLog.d(TAG,"onLocDiagnosticMessage,code1:${p1},code2:${p1},msg:${p2}");
    }

    override fun onReceiveVdrLocation(p0: BDLocation?) {
        super.onReceiveVdrLocation(p0)
        SLog.d(TAG,"onReceiveVdrLocation");
    }

    override fun onReceiveLocString(p0: String?) {
        super.onReceiveLocString(p0)
        SLog.d(TAG,"onReceiveLocString,data:${p0}");
    }
}
