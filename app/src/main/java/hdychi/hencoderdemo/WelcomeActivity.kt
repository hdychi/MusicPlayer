package hdychi.hencoderdemo

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import rx.Observable
import rx.Scheduler
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class WelcomeActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val subscriber = object : Subscriber< MutableList<Mp3Info>>(){
            override fun onNext(t: MutableList<Mp3Info>?) {
                MusicUtil.musicList.clear()
                MusicUtil.musicList.addAll(t!!.toList())
                jumpToMain()
                Toast.makeText(applicationContext,"加载文件完成",Toast.LENGTH_SHORT)
            }
            override fun onCompleted() {

            }
            override fun onError(e: Throwable?) {
                Toast.makeText(applicationContext,"加载文件错误",Toast.LENGTH_SHORT)
            }

        }
        Observable.just(this)
                .map { t -> MusicUtil.getMp3Infos(t) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }
    fun jumpToMain(){
        var intent = Intent()
        intent.setClass(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}