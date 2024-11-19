package Ui.data

data class Filter(val key:String,var value:String,val enabled:Boolean){
    constructor(parentKey:String,childKey:String,value:String ,enabled:Boolean):this("$parentKey->>$childKey",value,enabled)

}