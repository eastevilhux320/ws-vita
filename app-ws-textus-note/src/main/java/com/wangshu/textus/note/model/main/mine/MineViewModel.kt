package com.wangshu.textus.note.model.main.mine

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wangshu.textus.note.common.NoteViewModel
import com.wangshu.textus.note.entity.BudgetEntity
import com.wangshu.textus.note.network.NoteModel
import com.wsvita.account.entity.assets.AssetsEntity
import com.wsvita.account.network.model.AccountModel
import com.wsvita.account.network.response.AccountCenterReponse
import kotlinx.coroutines.launch

class MineViewModel(application: Application) : NoteViewModel(application) {

    val accountCenter = MutableLiveData<AccountCenterReponse>();
    val assets = MutableLiveData<AssetsEntity>();
    /**
     * 本月预算
     */
    val monthBudget = MutableLiveData<BudgetEntity>();

    override fun initModel() {
        super.initModel()
        loadData();
    }

    private fun loadData(){
        viewModelScope.launch {
            launch {
                accountCenter();
            }
            launch {
                monthBudget();
            }
        }
    }

    private suspend fun accountCenter(){
        val result = request(showLoading = false, requestCode = REQ_ACCOUNT_CENTER){
            AccountModel.instance.accountCenter();
        }
        result?.let {
            withMain {
                accountCenter.value = it;
                assets.value = it.userAssets;
            }
        }
    }

    private suspend fun monthBudget(){
        val result = request(showLoading = false, requestCode = REQ_MONTH_BUDGET){
            NoteModel.instance.budgetDetail(1,3);
        }
        result?.let {d->
            withMain {
                monthBudget.value = d.budget;
            }
        }
    }

    companion object{
        private const val TAG = "WS_Note_MAIN_NoteViewModel==>";
        private const val REQ_ACCOUNT_CENTER = 0x01;
        private const val REQ_MONTH_BUDGET = 0x02;
    }
}
