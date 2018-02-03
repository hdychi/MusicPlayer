package hdychi.hencoderdemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PersistableBundle
import android.provider.Contacts
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat
import kotlin.coroutines.experimental.CoroutineContext

class MainActivity : AppCompatActivity(){
    var playMusicService : PlayMusicService ?= null
    private val time = SimpleDateFormat("mm:ss")
    private val handler = Handler{_ ->
        val current = playMusicService?.mediaPlayer?.currentPosition ?:0
        nowTime.text = time.format(current)
        val all = playMusicService?.mediaPlayer?.duration ?: 0
        allTime.text = time.format(all)
        Log.i("kotlin thread",Thread.currentThread().name)
        music_bar.progress = Math.floor(current / (all.toDouble())  * 100).toInt()
        true
    }

    private val connection : ServiceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            playMusicService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playMusicService = (service as PlayMusicService.MyBinder).service
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent = Intent(this,PlayMusicService::class.java)
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
        initListener()

    }
    private fun initListener(){
        play.setOnClickListener { _-> playMusicService?.playMusic() }
        last.setOnClickListener { _-> playMusicService?.lastSong() }
        next.setOnClickListener { _-> playMusicService?.nextSong()}
        music_bar.setOnMoveListner { playMusicService?.seekTime(music_bar.progress) }
        Log.i("current thread",Thread.currentThread().name)
        runBlocking<Unit> {
            Log.i("blocking","did work")
            val thread = launch(CommonPool) {
               refresh()
            }
            thread.start()

        }

    }

    override fun onResume() {
        super.onResume()
        handler.sendEmptyMessage(0)
    }
    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
    suspend fun refresh(){
        Log.i("kotlin thread",Thread.currentThread().name)
        while(true) {
            delay(200)
            handler.sendEmptyMessage(0)
        }
    }
}