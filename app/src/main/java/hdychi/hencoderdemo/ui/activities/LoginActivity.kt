package hdychi.hencoderdemo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.UserBean
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
    private fun login(){
        val subscriber = object : Subscriber<UserBean>(){
            override fun onNext(t: UserBean?) {
                CommonData.setUser(t)
                CommonData.setLogin(true)
                jump()
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                this@LoginActivity.toast("登录失败")
                e?.printStackTrace()
            }
        }
        val client = ApiProvider()
        client.getUser(subscriber,phoneText.text.toString(),pwdText.text.toString())
    }
    private fun jump(){

        if(CommonData.getUser() ==null){
            return
        }
        Log.i("用户数据", Gson().toJson(CommonData.getUser()))
        val intent = Intent();
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}