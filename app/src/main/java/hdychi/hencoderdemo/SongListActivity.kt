package hdychi.hencoderdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.Result
import hdychi.hencoderdemo.bean.SpacesItemDecoration
import hdychi.hencoderdemo.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.activity_song_list.*
import rx.Subscriber

class SongListActivity : BaseActivity() {
    override fun getContentViewId() = R.layout.activity_song_list
    override fun getToolBar(): Toolbar = toolbar
    val  mAdapter = SongListAdapter()
    var id : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent?.getIntExtra("id",-1)?:-1
        song_list_recycler.layoutManager = LinearLayoutManager(this)
        song_list_recycler.adapter = mAdapter
        song_list_recycler.addItemDecoration(SpacesItemDecoration(2))
        mAdapter.onItemClickListener = OnItemClickListener { index ->  jump(index)}
        refresh()
    }
    private fun refresh(){
        if(id == -1){
            Toast.makeText(applicationContext,"歌单无效！",Toast.LENGTH_SHORT).show()
            return
        }

        val subscriber = object : Subscriber<Result>(){
            override fun onNext(t: Result?) {
                mAdapter.addAll(t?.tracks as MutableList)
                song_list_pic.setImageURI(Uri.parse(t.coverImgUrl))
                song_list_name.text = t.name
                song_list_creator_name.text = t.creator?.nickname
                song_list_creator_pic.setImageURI(Uri.parse(t.creator?.avatarUrl))
                loading.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                Toast.makeText(applicationContext,"加载歌单失败",Toast.LENGTH_SHORT).show()
                loading?.text = "加载失败"
            }
        }
        ApiProvider().getSongs(subscriber,id)
    }
    private fun jump(index : Int){
        val intent = Intent(this, PlayLocalActivity::class.java)
        intent.putExtra("id",mAdapter.getItem(index).id)
        CommonData.netMusicList.clear()
        CommonData.netMusicList.addAll(mAdapter.mItems)
        CommonData.mode = CommonData.NET
        startActivity(intent)
    }
}
