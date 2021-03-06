package hdychi.hencoderdemo.ui.activities


import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import hdychi.hencoderdemo.*
import hdychi.hencoderdemo.bean.TracksItem
import hdychi.hencoderdemo.interfaces.*
import hdychi.hencoderdemo.service.PlayNetService
import hdychi.hencoderdemo.support.*
import hdychi.hencoderdemo.ui.fragments.AlbumFragment
import hdychi.hencoderdemo.ui.fragments.LyricFrament
import kotlinx.android.synthetic.main.activity_play_net.*
import kotlinx.android.synthetic.main.play_bottom.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat

class PlayNetActivity : BaseActivity(),OnFragmentClickListener,OnChangeListener
        ,OnSeekToListener,PlayController{



    var playNetService : MediaAidlInterface? = null
    var onPauseMusicListener : OnPauseMusicListener? = null
    private val time = SimpleDateFormat("mm:ss")
    private var mIntent = Intent()
    private var activeFragment : Fragment = AlbumFragment.newInstance(0)
    private var coroutine : Job? = null
    private lateinit var notificationProvider : NotificationProvider
    private var isActive = false
    private val handler = Handler{_ ->

        try{
            if(playNetService!=null && playNetService!!.hasPrepared()){
                val current = playNetService!!.postion()
                nowTime.text = time.format(current)
                val all = playNetService!!.duration()
                allTime.text = time.format(all)
                music_bar.progress= (current / all.toDouble()).toFloat()
                if(playNetService!!.isPlaying()){
                    play.background = getDrawable(R.drawable.pause)
                }
                else{
                    play.background = getDrawable(R.drawable.play)
                }
                if(activeFragment is LyricFrament){
                    (activeFragment as LyricFrament)
                            .takeIf { t -> !t.isDragging() }
                            ?.refresh(current)
                }

            }
            else{
                nowTime.text = time.format(0);
                music_bar.progress = 0.0f
            }
            onPauseMusicListener?.onPauseMusic(playNetService?.isPlaying?:false)

        }
        catch(e : IllegalStateException){
            e.printStackTrace()
        }
        true
    }
    private val connection : ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            playNetService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            playNetService = MediaAidlInterface.Stub.asInterface(service)
            if(playNetService?.playingId != CommonData.getNetNowItemID()){
                playNetService?.setPlayList( mutableListOf<String>()
                        .let{ CommonData.getNetMusicList()
                                .filter { t -> t!=null }
                                .forEach {
                                    t -> it.add(t!!.id!!.toString())}
                            it
                        })
                playNetService?.setNowindex(CommonData.getNowIndex())
                playNetService?.reset()
            }

            coroutine = launch(CommonPool) {
                while(true){
                    refresh()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(playNetService == null) {
            mIntent = Intent(this, PlayNetService::class.java)
            bindService(mIntent, connection, Context.BIND_AUTO_CREATE)
        }
        initListener()
        notificationProvider  = NotificationProvider(this)
        notificationProvider.refresh(CommonData.getNetNowItemID())
    }

    override fun onResume() {
        super.onResume()
        initDisplay()
        initFrag(CommonData.getNetNowItemID(), CommonData.ALBUM_FRAGMENT_ID)

        isActive = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        isActive = false
    }

    override fun onDestroy(){
        super.onDestroy()
        playNetService?.stopService()
        unbindService(connection)
        coroutine?.cancel()
        notificationProvider.destroy()
    }

    override fun onBackPressed(){
        moveTaskToBack(true)
    }
    override fun pre() {
        CommonData.setNowIndex(CommonData.getNowIndex()-1)
        if (CommonData.getNowIndex()< 0) {
            CommonData.setNowIndex(CommonData.getNetMusicList().size - 1)
        }
        onChangeSong()
        playNetService?.prev(CommonData.getNowIndex())

    }

    override fun next() {
        CommonData.setNowIndex(CommonData.getNowIndex()+1)
        if (CommonData.getNowIndex()>= CommonData.getNetMusicList().size) {
            CommonData.setNowIndex(0)
        }
        onChangeSong()
        playNetService?.next(CommonData.getNowIndex())
    }

    override fun playOrPause() {
        playNetService?.playOrPause()
        onPauseMusicListener?.onPauseMusic(playNetService?.isPlaying?:false)
    }
    private fun initFrag(id : Int,type : Int){
        if(type == CommonData.ALBUM_FRAGMENT_ID){
            val frag = AlbumFragment.newInstance(id)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,frag)
                    .commit()
            onPauseMusicListener = frag
            activeFragment = frag
        }
        else{
            val frag = LyricFrament.newInstance(id)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frame,frag)
                    .commit()
            activeFragment = frag
        }

    }
    private fun initListener(){

        last.setOnClickListener { pre() }
        next.setOnClickListener { next() }
        music_bar.setOnMoveListner { playNetService?.seekProgress(music_bar.progress) }
        play.setOnClickListener { playOrPause() }

    }

    private fun initDisplay(){
        val nowItem : TracksItem = CommonData.getNetMusicList()[CommonData.getNowIndex()]
        toolbar.title = nowItem.name
        toolbar.subtitle = MusicUtil.getArtistsStr(nowItem.ar)
        if(toolbar.subtitle.length > 20){
            toolbar.subtitle = toolbar.subtitle.substring(0,17) + "..."
        }
    }
    private suspend fun refresh(){
        while(true) {
            delay(500)
            handler.sendEmptyMessage(0)
        }
    }
    override fun onFragmentClick(pos : Int) {
        when(pos){
            CommonData.ALBUM_FRAGMENT_ID -> initFrag(CommonData.getNetNowItemID(),CommonData.LYRIC_FRAGMENT_ID)
            CommonData.LYRIC_FRAGMENT_ID -> initFrag(CommonData.getNetNowItemID(),CommonData.ALBUM_FRAGMENT_ID)
        }
    }
    override fun onSeekTo(timeSecs: Int) {
        playNetService?.seekSec(timeSecs)
    }
    override fun onChangeSong() {
        initDisplay()
        if (isActive){
            initFrag(CommonData.getNetNowItemID(),CommonData.ALBUM_FRAGMENT_ID)
        }
        play.background = getDrawable(R.drawable.loading)
        notificationProvider.refresh(CommonData.getNetNowItemID())
    }

    override fun getContentViewId(): Int = R.layout.activity_play_net

    override fun getToolBar(): Toolbar = toolbar
}