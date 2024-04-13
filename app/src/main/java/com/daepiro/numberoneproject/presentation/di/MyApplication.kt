package com.daepiro.numberoneproject.presentation.di

import android.app.Application
import androidx.constraintlayout.widget.Constraints
import com.daepiro.numberoneproject.BuildConfig
import com.google.android.datatransport.cct.internal.NetworkConnectionInfo
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    val KAKAO = BuildConfig.KAKAO_NATIVE_APP_KEY

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,KAKAO)
    }

}