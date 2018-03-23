package hdychi.hencoderdemo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.CommonSubscriber
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.UserBean
import hdychi.hencoderdemo.interfaces.OnEndController
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.toast
import kotlinx.android.synthetic.main.activity_login.*
import rx.Subscriber

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(CommonData.isLogin()){
           jump()
        }
        loginButton.setOnClickListener { login() }
    }

    override fun onDestroy() {
        super.onDestroy()
        ApiProvider.unSubscribe(this)
    }
    private fun login(){
        loginButton.isActivated = false
        val subscriber = CommonSubscriber<UserBean>(this,"登录失败")
        subscriber.onSuccessController = OnSuccessController { t ->
            CommonData.setUser(t)
            CommonData.setLogin(true)
            jump()
        }
        subscriber.onEndController = OnEndController {
            loginButton.isActivated = true
        }
        ApiProvider.getUser(this,subscriber,phoneText.text.toString(),pwdText.text.toString())
    }
    private fun jump(){

        if(CommonData.getUser() ==null){
            return
        }
        Log.i("用户数据", Gson().toJson(CommonData.getUser()))
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}