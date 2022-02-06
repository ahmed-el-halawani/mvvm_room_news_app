package com.newcore.mvvmroomnewsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build
import android.view.View
import java.io.IOError
import java.io.IOException

object NetworkingHelper{

    fun hasInternet(context: Context):Boolean{
        val connectivityManager =  context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork?:return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
        return when{
            capabilities.hasTransport(TRANSPORT_WIFI)->true
            capabilities.hasTransport(TRANSPORT_CELLULAR)->true
            capabilities.hasTransport(TRANSPORT_ETHERNET)->true
            else->false
        }
    }

    fun safeResponseCaller(context:Context,safeCall:()->Unit){
        try {
            if(hasInternet(context)){
                safeCall()
            }else{

            }
        }catch (e:IOException){

        }
    }


}