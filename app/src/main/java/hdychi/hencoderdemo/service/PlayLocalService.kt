package hdychi.hencoderdemo.service

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.bean.Mp3Info
import hdychi.hencoderdemo.support.MusicUtil

class PlayLocalService : PlayService(){


    private var musicList: List<Mp3Info> = mutableListOf()
    override fun onBind(p0: Intent?): IBinder {
        return super.onBind(p0)
    }
    override fun resetPlayer() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(this,
                Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()
                        + "/" + getCurrentSong().id))
        isPlaying = false

    }

    override fun init() {
        musicList = CommonData.getLocalMusicList()

        musicSize = musicList.size
    }

    fun getPic(): Bitmap? {
        return MusicUtil.getArtwork(this,
                getCurrentSong().id,
                getCurrentSong().albumId, true, true)
    }

    fun getCurrentSong(): Mp3Info {
        return musicList[CommonData.getNowIndex()]
    }

}