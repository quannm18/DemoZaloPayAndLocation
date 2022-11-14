package com.quannm18.demozalopay

import android.app.Application
import android.os.StrictMode
import com.quannm18.demozalopay.utils.AppInfo
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

class MyApp : Application() {
    companion object {
        val environment: Environment = Environment.SANDBOX
    }

    override fun onCreate() {
        super.onCreate()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(AppInfo.APP_ID, environment)
    }
}