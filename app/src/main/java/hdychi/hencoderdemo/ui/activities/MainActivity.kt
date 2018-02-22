package hdychi.hencoderdemo.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.PlaylistItem
import hdychi.hencoderdemo.bean.SpacesItemDecoration
import hdychi.hencoderdemo.interfaces.OnItemClickListener
import hdychi.hencoderdemo.ui.adapters.PlayListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import rx.Subscriber
import android.app.ActivityManager.RunningTaskInfo
import android.app.ActivityManager
import android.content.Context
import android.support.v7.widget.Toolbar
import hdychi.hencoderdemo.DemoApp
import hdychi.hencoderdemo.support.isInTask
import hdychi.hencoderdemo.support.toast


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun getContentViewId() = R.layout.activity_main
    override fun getToolBar() = toolbar

    val mAdapter = PlayListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fab.setOnClickListener {
            val intent = Intent(applicationContext,PlayNetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            if (intent.isInTask(this@MainActivity)){
                startActivity(intent)
            }
            else{
                this@MainActivity.toast("没有音乐在播放！")
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)

        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val userName = nav_view.getHeaderView(0)
                .findViewById<TextView>(R.id.user_name)
        val userPhone = nav_view.getHeaderView(0)
                .findViewById<TextView>(R.id.user_phone)
        val photo = nav_view.getHeaderView(0)
                .findViewById<SimpleDraweeView>(R.id.photo)
        val headerBackground = nav_view.getHeaderView(0)
                .findViewById<SimpleDraweeView>(R.id.head_background)

        userName.text = CommonData.getUser()?.profile?.nickname
        userPhone?.text = CommonData.getUser()?.profile?.description
        photo.setImageURI(Uri.parse(CommonData.getUser()?.profile?.avatarUrl?:""))
        headerBackground.setImageURI(Uri.parse(CommonData.getUser()?.profile?.backgroundUrl?:""))

        play_swipe.setOnRefreshListener { refresh()}
        play_list.layoutManager = LinearLayoutManager(this)
        play_list.adapter = mAdapter
        play_list.addItemDecoration(SpacesItemDecoration(2))
        mAdapter.onItemClickListener = OnItemClickListener { index -> jump(index) }
        refresh()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            moveTaskToBack(true)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.logout -> {
                // Handle the camera action
                CommonData.setLogin(false)
                val intent = Intent(this@MainActivity,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.quit ->{
                DemoApp.clearActivity()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun refresh(){
        play_swipe.isRefreshing = true
        val subscriber = object : Subscriber<MutableList<PlaylistItem>>(){
            override fun onNext(t: MutableList<PlaylistItem>?) {
                mAdapter.addAll(t!!)
                play_swipe.isRefreshing = false
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                play_swipe.isRefreshing = false
                this@MainActivity.toast("获取用户歌单失败")
            }

        }
        ApiProvider().getPlayLists(subscriber)
    }
    private fun jump(index : Int){
        var intent = Intent()
        intent.action = "playList"
        intent.putExtra("id",mAdapter.get(index).id)
        startActivity(intent)
    }
}
