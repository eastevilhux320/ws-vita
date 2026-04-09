package com.wangshu.mira.local.manager

import com.wangshu.mira.entity.MiraUserEntity
import com.wsvita.framework.local.BaseManager

class MiraManager : BaseManager {
    private constructor(){

    }

    companion object {
        private const val TAG = "WSVita_F_M_MiraManager=>"
        val instance: MiraManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MiraManager() }

        private var miraUserId : String? = null;

        /**
         * 后端返回的用户id
         */
        private var userId : Long? = null;
    }


    override fun onInit() {

    }

    override fun init() {
        super.init()
    }

    fun getMiraUserId(): String? {
        return miraUserId
    }

    fun setUserId(userId : String){
        checkInit();
        MiraManager.miraUserId = userId;
    }

    fun userId(userId :  Long){
        if(userId > 0L){
            MiraManager.userId = userId;
        }
    }

    fun getUserId(): Long? {
        return userId
    }
}
