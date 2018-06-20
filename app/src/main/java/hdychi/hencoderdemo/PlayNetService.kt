package hdychi.hencoderdemo

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.orhanobut.logger.Logger
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.Mp3Info
import hdychi.hencoderdemo.bean.TracksItem
import hdychi.hencoderdemo.interfaces.OnChangeListener
import hdychi.hencoderdemo.interfaces.OnEndController
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.MyLog
import hdychi.hencoderdemo.support.showNotification
import hdychi.hencoderdemo.support.toast
import rx.Observable
import rx.Subscriber


class PlayNetService : Service(){

    var mediaPlayer : MediaPlayer = MediaPlayer()
    var hasPrepared  = false
    var playList : MutableList<Int> = mutableListOf()
    var nowIndex = 0
    override fun onBind(p0: Intent?): IBinder = MediaAidlInterfaceImpl()
    fun resetPlayer() {
        hasPrepared = false
        val subscriber = CommonSubscriber<String>(this,"播放失败")
        subscriber.onSuccessController = OnSuccessController {t ->
            mediaPlayer = MediaPlayer()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(t)
            mediaPlayer.setOnPreparedListener {
                t->t.start()
                hasPrepared = true
            }
            try{
                mediaPlayer.prepare()
            }
            catch(e : IllegalStateException){
                e.printStackTrace()
            }

        }
        ApiProvider.getMusicUrl(this,subscriber, playList[nowIndex])
        //this.showNotification(playList[nowIndex])
    }

    fun destroyPlayer(){
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    inner class MediaAidlInterfaceImpl: MediaAidlInterface.Stub(){
        override fun setPlayList(playList: MutableList<String>) {
            this@PlayNetService.playList.clear()
            playList.forEach {t->this@PlayNetService.playList.add(t.toInt())}
        }

        override fun setNowindex(index: Int) {
            nowIndex = index
        }

        override fun getInfo(): String {
            return "Service返回的字符串"
        }
        override fun seekSec(secs: Int) {
            mediaPlayer.seekTo(secs)
        }

        override fun seekProgress(progress: Float) {
            mediaPlayer.seekTo(Math.floor((progress * (mediaPlayer.duration))
                    .toDouble()).toInt())
        }

        override fun playOrPause() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()

            }
            else {
                mediaPlayer.start()
            }
        }

        override fun prev(index : Int) {
            nowIndex = index
            reset()
        }

        override fun next(index : Int) {
            nowIndex = index
            reset()
        }

        override fun duration(): Int = mediaPlayer.duration

        override fun postion(): Int = mediaPlayer.currentPosition


        override fun reset() {
            destroyPlayer()
            resetPlayer()
            MyLog("重置")
        }
        override fun stopService(){
            destroyPlayer()
            this@PlayNetService.stopSelf()
        }
        override fun hasPrepared() : Boolean = hasPrepared
        override fun isPlaying(): Boolean = mediaPlayer.isPlaying
        override fun getPlayingId():Int = if(playList.size>0) playList[nowIndex] else -1
    }

}
