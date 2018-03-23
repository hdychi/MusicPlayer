package hdychi.hencoderdemo.ui.activities


import android.content.*
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import hdychi.hencoderdemo.*
import hdychi.hencoderdemo.bean.TracksItem
import hdychi.hencoderdemo.interfaces.OnChangeListener
import hdychi.hencoderdemo.interfaces.OnFragmentClickListener
import hdychi.hencoderdemo.interfaces.OnPauseMusicListener
import hdychi.hencoderdemo.interfaces.OnSeekToListener
import hdychi.hencoderdemo.support.MusicUtil
import hdychi.hencoderdemo.ui.fragments.AlbumFragment
import hdychi.hencoderdemo.ui.fragments.LyricFrament
import kotlinx.android.synthetic.main.activity_play_net.*
import kotlinx.android.synthetic.main.play_bottom.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat

class PlayNetActivity : BaseActivity(),OnFragmentClickListener,OnChangeListener,
        MediaPlayer.OnPreparedListener,OnSeekToListener{


    var playNetService : PlayNetService?= null
    var onPauseMusicListener : OnPauseMusicListener? = null
    private val time = SimpleDateFormat("mm:ss")
    private var mIntent = Intent()
    private var activeFragment : Fragment = AlbumFragment.newInstance(0)
    private val handler = Handler{_ ->
        if(playNetService?.mediaPlayer!=null){
            val current = playNetService?.mediaPlayer?.currentPosition ?:0
            nowTime.text = time.format(current)
            val all = playNetService?.mediaPlayer?.duration ?: 0
            allTime.text = time.format(all)
            music_bar.progress= (current / all.toDouble()).toFloat()
            if(playNetService?.mediaPlayer?.isPlaying?:false){
                play.background = getDrawable(R.drawable.pause)
            }
            else{
                play.background = getDrawable(R.drawable.play)
            }
            if(activeFragment is LyricFrament){
                (activeFragment as LyricFrament)
                        .takeIf { t -> !t.isDragging() }
                        ?.refresh(playNetService?.mediaPlayer?.currentPosition?:0)
            }
        }

        true
    }
    private val connection : ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            playNetService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playNetService = (service as PlayNetService.MyBinder)
                    .getService(this@PlayNetActivity)
            playNetService?.resetPlayer(this@PlayNetActivity)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIntent.setClass(this, PlayNetService::class.java)
        bindService(mIntent,connection, Context.BIND_AUTO_CREATE)
        initDisplay()
        initFrag(CommonData.getNetNowItemID(),CommonData.ALBUM_FRAGMENT_ID)
        initListener()
    }

    override fun onResume() {
        super.onResume()
        onPauseMusicListener?.onPauseMusic(playNetService?.mediaPlayer?.isPlaying?:false)
    }
    override fun onDestroy() {
        super.onDestroy()
        playNetService?.destroyPlayer()
        stopService(mIntent)
        unbindService(connection)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
    private fun initFrag(id : Int,type : Int){
        if(type == CommonData.ALBUM_FRAGMENT_ID){
            val frag = AlbumFragment.newInstance(id)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,frag)
                    .commit()
            onPauseMusicListener = frag
            activeFragment = frag as Fragment
        }
        else{
            val frag = LyricFrament.newInstance(id)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,frag)
                    .commit()
            activeFragment = frag as Fragment
        }

    }
    private fun initListener(){

        last.setOnClickListener {
            playNetService?.lastSong(this)
            onChangeSong()
        }
        next.setOnClickListener {
            playNetService?.nextSong(this)
            onChangeSong()
        }
        music_bar.setOnMoveListner { playNetService?.seekTime(music_bar.progress) }
        play.setOnClickListener {
            playNetService?.playMusic()
            onPauseMusicListener?.onPauseMusic(playNetService?.mediaPlayer?.isPlaying?:false)
        }

        launch(CommonPool) {
            while(true){
                refresh()
            }
        }
    }

    private fun initDisplay(){
        val nowItem : TracksItem = CommonData.getNetMusicList()[CommonData.getNowIndex()]
        toolbar.title = nowItem.name
        toolbar.subtitle = MusicUtil.getArtistsStr(nowItem.artists)
        if(toolbar.subtitle.length > 20){
            toolbar.subtitle = toolbar.subtitle.substring(0,17) + "..."
        }
    }
    private suspend fun refresh(){
        while(true) {
            delay(200)
            handler.sendEmptyMessage(0)
        }
    }
    //TODO:切换fragment
    override fun onFragmentClick(pos : Int) {
        when(pos){
            CommonData.ALBUM_FRAGMENT_ID -> initFrag(CommonData.getNetNowItemID(),CommonData.LYRIC_FRAGMENT_ID)
            CommonData.LYRIC_FRAGMENT_ID -> initFrag(CommonData.getNetNowItemID(),CommonData.ALBUM_FRAGMENT_ID)
        }
    }
    override fun onSeekTo(timeSecs: Int) {
        playNetService?.mediaPlayer?.seekTo(timeSecs)
    }
    override fun onChangeSong() {
        initDisplay()
        initFrag(CommonData.getNetNowItemID(),CommonData.ALBUM_FRAGMENT_ID)
        play.background = getDrawable(R.drawable.loading)
    }

    override fun onPrepared(p0: MediaPlayer?) {

        playNetService?.mediaPlayer = p0!!
        onPauseMusicListener?.onPauseMusic(true)
    }
    override fun getContentViewId(): Int = R.layout.activity_play_net

    override fun getToolBar(): Toolbar = toolbar
}