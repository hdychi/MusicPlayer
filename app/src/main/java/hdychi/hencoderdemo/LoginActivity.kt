package hdychi.hencoderdemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.UserBean
import hdychi.hencoderdemo.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import rx.Subscriber

class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(Hawk.get("isLogin",false)){
           jump()
        }
        loginButton.setOnClickListener { login() }
    }
    private fun login(){
        val subscriber = object : Subscriber<UserBean>(){
            override fun onNext(t: UserBean?) {
                Hawk.put("isLogin",true)
                Hawk.put("user",t)
                jump()
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                Toast.makeText(applicationContext,"登录失败",Toast.LENGTH_SHORT).show()
                e?.printStackTrace()
            }
        }
        val client = ApiProvider()
        client.getUser(subscriber,phoneText.text.toString(),pwdText.text.toString())
    }
    private fun jump(){
        CommonData.user = Hawk.get("user")
        if(CommonData.user==null){
            return
        }
        Log.i("用户数据", Gson().toJson(CommonData.user))
        val intent = Intent();
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}