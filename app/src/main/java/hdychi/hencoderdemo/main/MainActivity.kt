package hdychi.hencoderdemo.main

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import rx.Subscriber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val mAdapter = PlayListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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

        userName.text = CommonData.user.profile?.nickname
        userPhone?.text = CommonData.user.profile?.description
        photo.setImageURI(Uri.parse(CommonData.user.profile?.avatarUrl))
        headerBackground.setImageURI(Uri.parse(CommonData.user.profile?.backgroundUrl))

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
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

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
                Toast.makeText(applicationContext,"获取用户歌单失败",Toast.LENGTH_SHORT).show()
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
