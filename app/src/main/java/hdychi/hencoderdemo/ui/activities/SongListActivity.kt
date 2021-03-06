package hdychi.hencoderdemo.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.CommonSubscriber
import hdychi.hencoderdemo.DemoApp
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.ui.adapters.SongListAdapter
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.Result
import hdychi.hencoderdemo.bean.SpacesItemDecoration
import hdychi.hencoderdemo.interfaces.OnEndController
import hdychi.hencoderdemo.interfaces.OnItemClickListener
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.isInTask
import hdychi.hencoderdemo.support.toast
import kotlinx.android.synthetic.main.activity_song_list.*
import rx.Subscriber

class SongListActivity : BaseActivity() {
    override fun getContentViewId() = R.layout.activity_song_list
    override fun getToolBar(): Toolbar = toolbar
    val  mAdapter = SongListAdapter()
    var id : Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent?.getLongExtra("id",-1)?:-1
        song_list_recycler.layoutManager = LinearLayoutManager(this)
        song_list_recycler.adapter = mAdapter
        song_list_recycler.addItemDecoration(SpacesItemDecoration(2))
        mAdapter.onItemClickListener = OnItemClickListener { index ->  jump(index)}
        fab.setOnClickListener {
            val intent = Intent(applicationContext,PlayNetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            if (intent.isInTask(this@SongListActivity)){
                startActivity(intent)
            }
            else{
                this@SongListActivity.toast("没有音乐在播放！")
            }
        }
        refresh()
    }

    private fun refresh(){
        if(id.compareTo(-1) == 0){
            this.toast("歌单无效！")
            return
        }

        val subscriber = CommonSubscriber<Result>(this,"加载歌单失败")
        subscriber.onSuccessController = OnSuccessController { t ->
            mAdapter.addAll(t?.tracks as MutableList)
            song_list_pic.setImageURI(Uri.parse(t.coverImgUrl))
            song_list_name.text = t.name
            song_list_creator_name.text = t.creator?.nickname
            song_list_creator_pic.setImageURI(Uri.parse(t.creator?.avatarUrl))
            loading.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
        }
        subscriber.onEndController = OnEndController {
            loading?.text = "加载失败"
        }
        ApiProvider.getSongs(this,subscriber,id)
    }
    private fun jump(index : Int){
        val intent = Intent(this, PlayNetActivity::class.java)
        intent.putExtra("id",mAdapter.getItem(index).id)
        CommonData.setNetMusicList(mAdapter.mItems)
        CommonData.setNowIndex(index)
        DemoApp.lists.filter { t -> t.get() is PlayNetActivity }
                .forEach { t -> t.get()?.finish() }
        startActivity(intent)
    }
}
