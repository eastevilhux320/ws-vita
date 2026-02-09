package com.wsvita.account.network.response

import com.wsvita.account.entity.account.AppAccountEntity
import com.wsvita.network.entity.BaseResponse

class AccountCenterReponse : BaseResponse() {

   var account: AppAccountEntity? = null;

   //var budget : BudgetEntity? = null;

   /**
    * 是否为vip用户
    */
   var isVip: Boolean = false;

   /**
    * vip信息
    */
   //var vipInfo : VipInfoEntity? = null;

   var totalPlan: Long = 0;

   var totalNote: Long = 0;

   var totalBill: Long = 0;

   /**
    * 瞬志总数量
    */
   var totalMomentum: Int = 0;

   var totalBox: Long = 0;

   /**
    * 用户当前等级
    */
   var userLevel: Int = 0;

   /**
    * 下一个等级
    */
   var nextLevel: Int = 0;

   /**
    * 下一个等级所需要的经验值
    */
   var nextLevelExp: Int = 0;

   /**
    * 当前等级
    */
   var currentLevel: Int = 0;

   /**
    * 当前经验值
    */
   var currentExp: Int = 0;

   /**
    * 下一个等级的比例值，[1-100]
    */
   var expIntrval: Int = 1;
}
