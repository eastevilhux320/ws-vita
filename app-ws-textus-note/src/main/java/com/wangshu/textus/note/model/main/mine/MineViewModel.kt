package com.wangshu.note.app.model.main.mine

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.transition.Slide
import com.starlight.dot.framework.utils.mainThread
import com.wangshu.note.app.R
import com.wangshu.note.app.common.NoteViewModel
import com.wangshu.note.app.entity.BudgetEntity
import com.wangshu.note.app.entity.assets.AssetsEntity
import com.wangshu.note.app.entity.main.MineFunctionEntity
import com.wangshu.note.app.model.main.MainData
import com.wangshu.note.app.network.model.NoteModel
import com.wangshu.note.app.network.reponse.NoteAccountCenterReponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MineViewModel(application: Application) : NoteViewModel<MainData>(application) {

    val accountCenter = MutableLiveData<NoteAccountCenterReponse>();
    val assets = MutableLiveData<AssetsEntity>();

    /**
     * 本月预算
     */
    val monthBudget = MutableLiveData<BudgetEntity>();

    val functionList = MutableLiveData<MutableList<MineFunctionEntity>>();

    override fun initData(): MainData {
        return MainData();
    }

    override fun initModel() {
        super.initModel()
        GlobalScope.launch {
            initFunctionList();
        }
    }

    override fun onResume() {
        super.onResume()
        accountCenter();
    }

    fun refresh(){
        if(isLogin()){
            accountCenter();
        }else{
            accountCenter.value = null;
            monthBudget.value = null;
            assets.value = null;
            showError(requestCode = MainData.RequestCode.CODE_MINE_REFRESH_NOLOGIN)
        }
    }

    private fun accountCenter() = GlobalScope.launch{
        val result = NoteModel.instance.noteAccountCenter();
        if(result.isSuccess){
            mainThread {
                accountCenter.value = result.data;
                assets.value = result.data?.userAssets;
                monthBudget.value = result.data?.budget;
            }
        }else{
            mainThread {
                accountCenter.value = null;
                assets.value = null;
                monthBudget.value = null;
            }
        }
    }

    private suspend fun initFunctionList(){
        val list = mutableListOf<MineFunctionEntity>();
        //提现记录
        val mw = MineFunctionEntity();
        mw.id = 1L;
        mw.iconResId = R.drawable.ic_mine_function_withdrawal_record;
        mw.functionText = getString(R.string.mine_function_withdrawal_record)
        list.add(mw);

        //收益记录
        val iw = MineFunctionEntity();
        iw.id = 2L;
        iw.functionText = getString(R.string.mine_function_income_record)
        iw.iconResId = R.drawable.ic_mine_function_income_recrod
        list.add(iw);

        mainThread {
            functionList.value = list;
        }
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_NoteViewModel==>";
    }
}
