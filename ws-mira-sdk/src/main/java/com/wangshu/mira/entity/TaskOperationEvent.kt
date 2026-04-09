package com.wangshu.mira.entity

import com.wangshu.mira.entity.enums.MiraOperatetion

class TaskOperationEvent {
    private var operatetion : MiraOperatetion;

    var code : Int? = null;

    var msg : String = "";

    var task : TaskEntity? = null;

    var targetApp : TaskAppEntity? = null;

    companion object{

        fun create(operatetion: MiraOperatetion): TaskOperationEvent {
            val op = TaskOperationEvent(operatetion);
            return op;
        }

        fun create(operatetion: MiraOperatetion,task: TaskEntity): TaskOperationEvent {
            val op = TaskOperationEvent(operatetion);
            op.task = task;
            return op;
        }
    }

    private constructor(operatetion : MiraOperatetion){
        this.operatetion = operatetion;
    }

    fun setCode(code : Int): TaskOperationEvent {
        this.code = code;
        return this;
    }

    fun setMessage(msg : String?): TaskOperationEvent {
        this.msg = msg?:"";
        return this;
    }

    fun setTask(task : TaskEntity): TaskOperationEvent {
        this.task = task;
        return this;
    }

    fun setApp(app : TaskAppEntity?): TaskOperationEvent {
        this.targetApp = app;
        return this;
    }

    fun operatetion(): MiraOperatetion {
        return operatetion;
    }
}
