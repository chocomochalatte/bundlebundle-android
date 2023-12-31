package com.example.bundlebundle.member

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.bundlebundle.R
import com.example.bundlebundle.product.list.ProductPageActivity
import com.example.bundlebundle.retrofit.ApiClient
import com.example.bundlebundle.retrofit.dataclass.member.LoginTokenVO
import com.example.bundlebundle.retrofit.dataclass.member.MemberVO
import com.example.bundlebundle.template.BaseTemplateActivity
import com.example.bundlebundle.util.LessonDeleteDialog
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@HiltAndroidApp
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keyHash = Utility.getKeyHash(this)
        Log.d("hasg",keyHash);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //컨텍스트 얻기
        val context: Context = this
        var mContext: Context? = null
        fun setContext(context: Context) {
            mContext = context
        }

        val showToast = intent.getBooleanExtra("LogoutToast", false)
        if (showToast) {
            toast();
        }
        val kakaoLoginButton = findViewById<android.widget.Button>(R.id.oauth_login)
        val basicLoginButton = findViewById<android.widget.Button>(R.id.btn_login)

        basicLoginButton.setOnClickListener {
            val dialog = LessonDeleteDialog(this)
            dialog.listener = object : LessonDeleteDialog.LessonDeleteDialogClickedListener {
                override fun onDeleteClicked() {
                    // 확인 버튼을 클릭했을 때 수행할 동작을 정의합니다.
                    // 예를 들어, 로그인 기능을 실행하는 코드를 추가할 수 있습니다.
                }
            }
            dialog.start()
        }

        // 카카오 로그인
        kakaoLoginButton.setOnClickListener{

            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨

            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("LOGIN", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {

                    val apiService = ApiClient.apiService
                    val call: Call<LoginTokenVO> = apiService.gettoken(token.accessToken)
                    call.enqueue(object : Callback<LoginTokenVO> {
                        override fun onResponse(call: Call<LoginTokenVO>, response: Response<LoginTokenVO>) {
                            if (response.isSuccessful) {
                                val tokenInfo = response.body()
                                tokenInfo?.let { info ->
                                    // 서버 응답 처리
                                    Log.i("RealLOGIN", "카카오계정으로 로그인 성공 :  ${info.token}")
                                    ApiClient.setJwtToken(info.token)

                                    val intent = Intent(this@LoginActivity, ProductPageActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    intent.putExtra("showToast", true)
                                    startActivity(intent)
                                    finish()

                                } ?: run {
                                    // 응답이 null인 경우 처리
                                    Log.e("RealLOGIN", "서버 응답이 null입니다.")
                                }
                            } else {
                                // 응답이 실패한 경우 처리
                                Log.e("RealLOGIN", "서버 응답이 실패했습니다. 상태 코드: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<LoginTokenVO>, t: Throwable) {
                            Log.e("RealLOGIN", "서버 응답이 실패했습니다. 상태 코드: ${t.printStackTrace()}")
                        }
                    })

                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    if (error != null) {
                        Log.e("LOGIN", "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                    } else if (token != null) {
                        Log.i("RealLOGIN", "카카오톡으로 로그인 성공 카카오톡있음1: ${token.accessToken}")

                        val apiService = ApiClient.apiService
                        val call: Call<LoginTokenVO> = apiService.gettoken(token.accessToken)
                        Log.i("RealLOGIN", "카카오 엑세스 토큰 카카오톡있음2:  ${token.accessToken}")
                        call.enqueue(object : Callback<LoginTokenVO> {
                            override fun onResponse(call: Call<LoginTokenVO>, response: Response<LoginTokenVO>) {
                                if (response.isSuccessful) {
                                    val tokenInfo = response.body()
                                    tokenInfo?.let { info ->
                                        // 서버 응답 처리
                                        Log.i("RealLOGIN", "카카오계정으로 로그인 성공 카카오톡있음3:  ${info.token}")
                                        ApiClient.setJwtToken(info.token)

                                        val intent = Intent(this@LoginActivity, ProductPageActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        intent.putExtra("showToast", true)
                                        startActivity(intent)
                                        finish()

                                    } ?: run {
                                        // 응답이 null인 경우 처리
                                        Log.e("RealLOGIN", "서버 응답이 null입니다.")
                                    }
                                } else {
                                    // 응답이 실패한 경우 처리
                                    Log.e("RealLOGIN", "서버 응답이 실패했습니다. 상태 코드: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<LoginTokenVO>, t: Throwable) {
                                Log.e("RealLOGIN", "서버 응답이 실패했습니다. 상태 코드: ${t.printStackTrace()}")
                            }
                        })

                        val intent = Intent(this, ProductPageActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }

        val imageView = findViewById<ImageView>(R.id.hyundai_food_img)
        imageView.setOnClickListener {
            startActivity((Intent(this@LoginActivity, ProductPageActivity::class.java)))
        }
    }

    fun kakaoLogout() {
        // 로그아웃
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("Hello", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            } else {
                Log.i("Hello", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }

    fun kakaoUnlink() {
        // 연결 끊기
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("Hello", "연결 끊기 실패", error)
            } else {
                Log.i("Hello", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
        finish()
    }

    private fun toast() {
        var layoutInflater = LayoutInflater.from(this).inflate(R.layout.toast_view_holder,null)
        var text : TextView = layoutInflater.findViewById(R.id.TextViewToast)
        text.text="로그아웃에 성공하였습니다"
        var toast = Toast(this)
        toast.view = layoutInflater
        //toast.setGravity(Gravity.TOP,0,400);
        toast.show()
    }
}
