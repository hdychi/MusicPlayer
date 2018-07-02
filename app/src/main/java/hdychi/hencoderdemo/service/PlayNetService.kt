package hdychi.hencoderdemo.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import hdychi.hencoderdemo.CommonSubscriber
import hdychi.hencoderdemo.MediaAidlInterface
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.MyLog


class PlayNetService : Service(){

    var mediaPlayer : MediaPlayer = MediaPlayer()
    var hasPrepared  = false
    var playList : MutableList<Int> = mutableListOf()
    var nowIndex = 0
    override fun onBind(p0: Intent?): IBinder = MediaAidlInterfaceImpl()
    fun resetPlayer() {
        hasPrepared = false
        val subscriber = CommonSubscriber<String>(this, "播放失败")
        subscriber.onSuccessController = OnSuccessController {t ->

            try{
                mediaPlayer.setDataSource(t)
                mediaPlayer.setOnPreparedListener {
                    t->t.start()
                    hasPrepared = true
                    MyLog("资源准备好了2")
                }
                mediaPlayer.prepareAsync()
            }
            catch(e : IllegalStateException){
                e.printStackTrace()
            }

        }
        ApiProvider.getMusicUrl(this,subscriber, playList[nowIndex])
        MyLog("开始准备资源")
    }

    fun destroyPlayer(){
        if(hasPrepared && mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
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
        override fun isPlaying(): Boolean {
            if(!hasPrepared()){
                return false
            }
            return mediaPlayer.isPlaying
        }
        override fun getPlayingId():Int = if(playList.size>0) playList[nowIndex] else -1
    }

}
