package com.wsvita.framework.router.contract

class ContractResult<O> {

    var action : String

    var name : String

    var data : O? = null;

    constructor(action : String,name : String){
        this.action = action;
        this.name = name;
    }
}
