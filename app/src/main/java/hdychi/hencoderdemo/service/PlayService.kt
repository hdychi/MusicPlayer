package hdychi.hencoderdemo.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.interfaces.OnChangeListener

abstract class PlayService : Service(){
    var mediaPlayer = MediaPlayer()
    var isPlaying = false
    var musicSize = 0
    var onChangeListener: OnChangeListener? = null
    abstract fun resetPlayer()
    abstract fun init()

    override fun onBind(p0: Intent?): IBinder {
        init()
        resetPlayer()
        return MyBinder()
    }
    
    fun playMusic() {
        Log.i("父类Mediaplayer状态",
                mediaPlayer.duration.toString())
        isPlaying = !isPlaying
        if (isPlaying) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }

    fun lastSong() {
        CommonData.setNowIndex(CommonData.getNowIndex() - 1)
        if (CommonData.getNowIndex() < 0) {
            CommonData.setNowIndex(musicSize - 1)
        }
        resetPlayer()
    }

    fun nextSong() {
        CommonData.setNowIndex(CommonData.getNowIndex() + 1)
        if (CommonData.getNowIndex() >= musicSize) {
            CommonData.setNowIndex(0)
        }

        resetPlayer()

    }
    fun seekTime(progress: Float) {
        mediaPlayer.seekTo(Math.floor((progress * mediaPlayer.duration).toDouble()).toInt())
    }
    inner class MyBinder : Binder() {
        internal fun getService(listener: OnChangeListener,className : String): PlayService {
            this@PlayService.onChangeListener = listener
            if(className == PlayLocalService::class.java.name){
                return PlayLocalService()
            }
            else{
                return PlayLocalService()
            }
        }
    }
    
}