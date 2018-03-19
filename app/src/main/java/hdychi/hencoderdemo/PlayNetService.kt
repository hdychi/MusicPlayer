package hdychi.hencoderdemo

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.Mp3Info
import hdychi.hencoderdemo.bean.TracksItem
import hdychi.hencoderdemo.interfaces.OnChangeListener
import hdychi.hencoderdemo.support.toast
import rx.Observable
import rx.Subscriber


class PlayNetService : Service() {

    var mediaPlayer : MediaPlayer? = MediaPlayer()
    var onPreparedListener : MediaPlayer.OnPreparedListener? = null

    override fun onBind(p0: Intent?): IBinder {
        return MyBinder()
    }
    fun resetPlayer(listener: MediaPlayer.OnPreparedListener) {
        destroyPlayer()
        val subscriber = object : Subscriber<String>(){
            override fun onNext(t: String?) {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(t)
                mediaPlayer?.setOnPreparedListener {it->
                    playMusic()
                    listener.onPrepared(it)
                }
                mediaPlayer?.prepareAsync()
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                applicationContext.toast("播放失败")
            }

        }
        ApiProvider.getMusicUrl(subscriber,
                CommonData.getNetMusicList()[CommonData.getNowIndex()].id)

    }
    fun playMusic() {

        if (mediaPlayer?.isPlaying?:false) {
            mediaPlayer?.pause()

        }
        else {
            mediaPlayer?.start()
        }
    }

    fun lastSong(listener: MediaPlayer.OnPreparedListener) {
        CommonData.setNowIndex(CommonData.getNowIndex()-1)
        if (CommonData.getNowIndex()< 0) {
            CommonData.setNowIndex(CommonData.getNetMusicList().size - 1)
        }

        resetPlayer(listener)
    }

    fun nextSong(listener: MediaPlayer.OnPreparedListener) {

        CommonData.setNowIndex(CommonData.getNowIndex()+1)
        if (CommonData.getNowIndex()>= CommonData.getNetMusicList().size) {
            CommonData.setNowIndex(0)
        }
        resetPlayer(listener)

    }
    fun seekTime(progress: Float) {
        mediaPlayer?.seekTo(Math.floor((progress * (mediaPlayer?.duration?:0))
                .toDouble()).toInt())
    }
    fun destroyPlayer(){
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    private fun showNotification(){
        val mBuilder = NotificationCompat.Builder(this)

    }
    inner class MyBinder : Binder() {
        internal fun getService(listener: MediaPlayer.OnPreparedListener)
                : PlayNetService {
            this@PlayNetService.onPreparedListener = listener
            return PlayNetService()

        }
    }

}
