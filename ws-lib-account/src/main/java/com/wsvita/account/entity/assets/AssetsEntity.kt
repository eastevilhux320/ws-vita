package com.wsvita.account.entity.assets

import com.wsvita.core.common.BaseEntity
import ext.BigDecimalExt.scale
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

/**
 * 用户资产实体类
 * @author Eastevil
 * @version 1.0.0
 * @date 2023/2/21 13:55
 */
class AssetsEntity : BaseEntity(),Serializable {

    var userId : Long = 0;
    var integral :Long = 0;
    //余额
    var balance : BigDecimal = BigDecimal.ZERO;
    var couponNum : Int = 0;

    //总收入
    var totalIncome : BigDecimal = BigDecimal.ZERO;

    /**
     *总支出
     */
    var totalExpenditure : BigDecimal = BigDecimal.ZERO;

    /**
     * 用户当前经验值
     */
    var exp : Int = 0;

    /**
     * 广告收益余额
     */
    var cashBalance : BigDecimal = BigDecimal.ZERO;

    /**
     * 云闪币数量
     */
    var yunshanCoin : Int = 0;

    var createDate : Long = System.currentTimeMillis();

    /**
     * 如果[recyclerItemType]方法返回为[com.star.starlight.ui.view.commons.RecyclerItemType]中定义的自定义布局展示类型，
     * 展示的item资源布局将通过调用次方法获得
     * create by Eastevil at 2022/10/28 17:15
     * @author Eastevil
     * @param
     * @return
     */
    override fun customLayoutId(): Int {
        return 0;
    }


    val integralText : String
        get() {
            return integral.toString();
        }

    val balanceText : String
        get() {
            return balance.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        }

    val cashBalanceText : String
        get() {
            return cashBalance.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        }

    val couponNumText : String
        get() {
            return couponNum.toString();
        }

    val totalIncomeText : String
        get() {
            return totalIncome.scale(2);
        }

    val totalExpenditureText : String
        get() {
            return totalExpenditure.scale(2)
        }

    val coinText : String
        get() {
            return yunshanCoin.toString();
        }

    val adBalanceText : String
        get() {
            return getString(com.wsvita.ui.R.string.app_yuan_format,cashBalanceText);
        }


}
