package hdychi.hencoderdemo.ui.activities

import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.Toolbar
import android.view.View
import hdychi.hencoderdemo.CommonSubscriber
import hdychi.hencoderdemo.ILinearLayoutManager
import hdychi.hencoderdemo.support.MusicUtil
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.CommentResponse
import hdychi.hencoderdemo.bean.SongsItem
import hdychi.hencoderdemo.bean.SpacesItemDecoration
import hdychi.hencoderdemo.interfaces.OnEndController
import hdychi.hencoderdemo.interfaces.OnErrorController
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.toast
import hdychi.hencoderdemo.ui.adapters.CommentAdapter
import kotlinx.android.synthetic.main.activity_comment.*
import rx.Subscriber
import rx.exceptions.OnErrorNotImplementedException

class CommentActivity : BaseActivity(){
    override fun getContentViewId() = R.layout.activity_comment

    override fun getToolBar(): Toolbar = toolbar

    val DEFAUT_LIMIT = 15
    val goodCmtAdapter = CommentAdapter(false, this)
    val newCmtAdapter = CommentAdapter(true, this)
    var page = 0
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent?.getIntExtra("id",-1)
        good_comment.layoutManager = ILinearLayoutManager(this)
        new_comment.layoutManager = ILinearLayoutManager(this)
        good_comment.adapter = goodCmtAdapter
        new_comment.adapter = newCmtAdapter
        good_comment.addItemDecoration(SpacesItemDecoration(1))
        new_comment.addItemDecoration(SpacesItemDecoration(1))

        scrollView.setOnScrollChangeListener {
            v: NestedScrollView, scrollX: Int, scrollY: Int,
            _, _->

            if (!isLoading && scrollY == (v.getChildAt(0).measuredHeight- v.measuredHeight)) {

                 getData(id?:-1)
            }
        }
        getData(id?:-1)
        initSongInf(id?:-1)
    }
    private fun initSongInf(id : Int){
        val subscriber = CommonSubscriber<SongsItem>(this,"获取歌曲失败")
        subscriber.onSuccessController = OnSuccessController {t ->
            song_pic.setImageURI(t.al?.picUrl)
            activity_cmt_nickname.text = t.name
            activity_cmt_artist.text = MusicUtil.getArStr(t.ar)
        }
        subscriber.onErrorController = OnErrorController {
            loading.text = "加载失败"
        }

        ApiProvider.getSongDetail(this,subscriber,id)
    }
    private fun getData(id : Int){
        isLoading = true
        val subscriber = CommonSubscriber<CommentResponse>(this,"获取评论失败")
        subscriber.onSuccessController = OnSuccessController { t ->
            if(page == 0){
                goodCmtAdapter.addAll(t.hotComments)
                loading.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
            }
            newCmtAdapter.appendAll(t.comments)
            page++
        }
        subscriber.onEndController = OnEndController {
            isLoading = false
        }

       ApiProvider.getSongComment(this,subscriber,id,DEFAUT_LIMIT,page * DEFAUT_LIMIT)
    }
}