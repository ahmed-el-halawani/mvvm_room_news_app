package com.newcore.mvvmroomnewsapp.utils

sealed class Resource<T>(
    var data: T? = null,
    var message:String? = null
) {

    class Success<T>(data:T) : Resource<T>(data)
    class Error<T>(message: String?=null,data: T?=null) : Resource<T>(data,message)
    class Loading<T> : Resource<T>()

}