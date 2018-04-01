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
import android.widget.Toast
import com.orhanobut.logger.Logger
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.Mp3Info
import hdychi.hencoderdemo.bean.TracksItem
import hdychi.hencoderdemo.interfaces.OnChangeListener
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.MyLog
import hdychi.hencoderdemo.support.toast
import rx.Observable
import rx.Subscriber


class PlayNetService : Service(){

    var mediaPlayer : MediaPlayer = MediaPlayer()
    var hasPrepared  = false
    override fun onBind(p0: Intent?): IBinder = MediaAidlInterfaceImpl()
    fun resetPlayer() {
        hasPrepared = false
        val subscriber = CommonSubscriber<String>(this,"播放失败")
        subscriber.onSuccessController = OnSuccessController {t ->
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(t)
            mediaPlayer.prepare()
            hasPrepared = true
        }
        //TODO:修复多进程静态变量失效问题
        ApiProvider.getMusicUrl(this,subscriber,
                CommonData.getNetMusicList()[CommonData.getNowIndex()].id)

    }

    fun destroyPlayer(){
        mediaPlayer.reset()
        mediaPlayer.release()
    }
    private fun showNotification(){
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)

    }
    inner class MediaAidlInterfaceImpl: MediaAidlInterface.Stub(){
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
            MyLog("播放暂停")
        }

        override fun prev() {
            CommonData.setNowIndex(CommonData.getNowIndex()-1)
            if (CommonData.getNowIndex()< 0) {
                CommonData.setNowIndex(CommonData.getNetMusicList().size - 1)
            }

            reset()
        }

        override fun next() {
            CommonData.setNowIndex(CommonData.getNowIndex()+1)
            if (CommonData.getNowIndex()>= CommonData.getNetMusicList().size) {
                CommonData.setNowIndex(0)
            }
            reset()
        }

        override fun duration(): Int = mediaPlayer.duration

        override fun postion(): Int = mediaPlayer.currentPosition


        override fun reset() {
            destroyPlayer()
            resetPlayer()
            playOrPause()
            MyLog("重置")
        }

        override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    }

}
