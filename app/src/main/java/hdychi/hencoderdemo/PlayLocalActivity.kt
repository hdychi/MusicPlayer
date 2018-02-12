package hdychi.hencoderdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import hdychi.hencoderdemo.interfaces.OnChangeListener
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.coroutines.experimental.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat

class PlayLocalActivity : AppCompatActivity(), OnChangeListener {


    var playLocalService: PlayLocalService?= null
    private val time = SimpleDateFormat("mm:ss")
    private val musicId : Int?  = null
    private val handler = Handler{_ ->
        val current = playLocalService?.mediaPlayer?.currentPosition ?:0
        nowTime.text = time.format(current)
        val all = playLocalService?.mediaPlayer?.duration ?: 0
        allTime.text = time.format(all)
        music_bar.progress= (current / all.toDouble()).toFloat()
        if(playLocalService?.mediaPlayer?.isPlaying?:false){
            play.background = getDrawable(R.drawable.pause)
        }
        else{
            play.background = getDrawable(R.drawable.play)
        }

        true
    }

    private val connection : ServiceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            playLocalService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playLocalService = (service as PlayLocalService.MyBinder)
                    .getService(this@PlayLocalActivity)
            onChange()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        var intent = Intent(this, PlayLocalService::class.java)
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
        initListener()
    }
    private fun initListener(){

        play.setOnClickListener { playLocalService?.playMusic() }
        last.setOnClickListener { playLocalService?.lastSong() }
        next.setOnClickListener { playLocalService?.nextSong()}
        music_bar.setOnMoveListner { playLocalService?.seekTime(music_bar.progress) }
        runBlocking<Unit> {
            Log.i("blocking","did work")
            val thread = launch(CommonPool) {
                refresh()
            }
            thread.start()

        }
        Log.i("current thread",Thread.currentThread().name)
    }

    override fun onResume() {
        super.onResume()
        handler.sendEmptyMessage(0)
    }
    override fun onChange() {
        Log.i("接口","调用")
        Observable.just(playLocalService?.pic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    t -> album_pic.setImageBitmap(t)
                    song_name.text = playLocalService?.currentSong?.title
                    album_name.text = playLocalService?.currentSong?.album
                }
    }
    private suspend fun refresh(){
        while(true) {
            delay(200)
            handler.sendEmptyMessage(0)
        }
    }

}