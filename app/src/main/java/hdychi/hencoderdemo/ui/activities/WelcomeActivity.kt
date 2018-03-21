package hdychi.hencoderdemo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.CommonSubscriber
import hdychi.hencoderdemo.support.MusicUtil
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.bean.Mp3Info
import hdychi.hencoderdemo.support.toast

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class WelcomeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val subscriber = object : Subscriber< MutableList<Mp3Info>>(){
            override fun onNext(t: MutableList<Mp3Info>?) {
                CommonData.setLocalMusicList(t!!.toList())
                applicationContext.toast("登录成功")
                jumpToMain()
            }
            override fun onCompleted() {

            }
            override fun onError(e: Throwable?) {
                applicationContext.toast("加载文件错误")
            }

        }
        Observable.just(this)
                .map { t -> MusicUtil.getMp3Infos(t) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }
    fun jumpToMain(){
        val intent = Intent()
        intent.setClass(this, PlayLocalActivity::class.java)
        startActivity(intent)
        finish()
    }
}