package com.daepiro.numberoneproject.presentation.view.login

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.daepiro.numberoneproject.R
import com.daepiro.numberoneproject.databinding.ActivitySplashBinding
import com.daepiro.numberoneproject.presentation.base.BaseActivity
import com.daepiro.numberoneproject.presentation.util.Extensions.repeatOnStarted
import com.daepiro.numberoneproject.presentation.util.TokenManager
import com.daepiro.numberoneproject.presentation.view.MainActivity
import com.daepiro.numberoneproject.presentation.view.networkerror.NetworkDialogFragment
import com.daepiro.numberoneproject.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val DURATION_TIME = 6000L    // 스플래시 화면 지연시간
    private lateinit var cm : ConnectivityManager
    val splashVM by viewModels<SplashViewModel>()
    @Inject lateinit var tokenManager: TokenManager
    private var isNetworkAvailable = false

    private val networkCallBack = object : ConnectivityManager.NetworkCallback() {
        // 네트워크가 연결된 경우
        override fun onAvailable(network: Network) {
            isNetworkAvailable = true
        }
        // 네트워크가 연결되지 않은 경우
        override fun onLost(network: Network) {
            isNetworkAvailable = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val builder = NetworkRequest.Builder()
        cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(builder.build(),networkCallBack)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.ltSplash.cancelAnimation()
            checkAutoLogin()
        }, DURATION_TIME)
        lifecycleScope.launch {
            splashVM.getSheltersetLocal()
        }
    }


    /** 자동로그인 가능한지 확인 **/
    private fun checkAutoLogin() {
        repeatOnStarted {
            val accessToken = tokenManager.accessToken.first()
            Log.d("taag SplashActivity", accessToken)

            if (isNetworkAvailable) {      // 네트워크가 연결된 경우
                if (accessToken.isNotEmpty()) {
                    // 카카오톡 공유에서 초대받고 들어 온 경우
                    if (Intent.ACTION_VIEW == intent.action) {
                        val uri = intent.data
                        if (uri != null) {
                            val userToken = uri.getQueryParameter("memberId")

                            if (userToken != null) {
                                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                                intent.putExtra("memberId", userToken)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                showToast("네트워크 연결 상태를 확인하세요.")
                showErrorDialog()
            }
        }
    }

    private fun showErrorDialog(){
        val dialog = NetworkDialogFragment()
        dialog.show(supportFragmentManager, "netWorkDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        cm.unregisterNetworkCallback(networkCallBack)
    }
}