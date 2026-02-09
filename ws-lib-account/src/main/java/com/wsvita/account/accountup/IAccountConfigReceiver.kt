package com.wsvita.account.accountup

interface IAccountConfigReceiver {

    fun onAccountConfigReady(provider: IAccountConfigProvider)
}
