package com.wangshu.mira.model.chrono

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.mira.commons.MiraViewModel
import com.wangshu.mira.entity.ChronoMenuEntity
import com.wangshu.mira.entity.SubmitRecordEntity
import com.wangshu.mira.network.model.MiraModel
import com.wsvita.core.configure.ModelRequestConfig
import com.wsvita.framework.entity.VError
import com.wsvita.framework.utils.SLog
import kotlinx.coroutines.launch

class ChronoViewModel(application: Application) : MiraViewModel(application) {
    private var isFirstCome : Boolean = false;
    private var page : Long = 1L;
    private var type : Int? = null;

    private val _menuList = MutableLiveData<MutableList<ChronoMenuEntity>>();
    val menuList : LiveData<MutableList<ChronoMenuEntity>>
        get() = _menuList;

    private val _submitRecordList = MutableLiveData<MutableList<SubmitRecordEntity>?>();
    val submitRecordList : LiveData<MutableList<SubmitRecordEntity>?>
        get() = _submitRecordList;

    private val _typeSelectedIndex = MutableLiveData<Int>();
    val typeSelectedIndex : LiveData<Int>
        get() = _typeSelectedIndex;

    override fun initModel() {
        super.initModel()
        isFirstCome = true;
        menuList();
    }

    fun queryTypeList(type : Int,position : Int){
        this.type = type;
        _submitRecordList.value?.let {
            it.clear();
            _submitRecordList.value = it;
        }
        _typeSelectedIndex.value = position;
        submitRecordList(true);
    }

    private fun menuList() = viewModelScope.launch{
        val mList = request(REQ_TASK_MENU_LIST,true){
            MiraModel.instance.chronoMenuList();
        }
        if(mList != null && mList.isNotEmpty()){
            withMain {
                _menuList.value = mList!!;
            }
            //设置默认第一次选中
            if(isFirstCome){
                //记录中一定还没有数据，不需要去清除
                isFirstCome = false;
                //记录type类型，调用查询记录的时候，直接使用即可
                type = mList.get(0).type;
                submitRecordList(false);
                withMain {
                    //默认选中第一位
                    _typeSelectedIndex.value = 0;
                }
            }
        }
    }

    private fun submitRecordList(showLoading : Boolean = false) = viewModelScope.launch{
        val submitRecord = request(REQ_SUBMIT_RECORD,showLoading) {
            MiraModel.instance.submitRecords(page,type);
        }
        submitRecord?.list?.let {
            var sList = _submitRecordList.value;
            if(sList == null){
                sList = mutableListOf()
            }
            sList.addAll(it);
            withMain {
                _submitRecordList.value = sList!!;
            }
        }
    }

    override suspend fun handleBusinessEmpty(config: ModelRequestConfig, code: Int, msg: String?) {
        super.handleBusinessEmpty(config, code, msg)
        SLog.d(TAG,"handleBusinessEmpty,code:${code},msg:${msg}");
    }

    override suspend fun handleBusinessError(config: ModelRequestConfig, error: VError) {
        super.handleBusinessError(config, error)
        SLog.d(TAG,"handleBusinessError,code:${error.code},msg:${error.msg}");
        withMain {
            _submitRecordList.value = null;
        }
    }

    companion object {
        private const val TAG = "Mira_Main_ChronoViewModel=>"
        private const val REQ_TASK_MENU_LIST = 0x01;
        private const val REQ_SUBMIT_RECORD = 0x02;
    }
}
