package hdychi.hencoderdemo.ui.activities

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import hdychi.hencoderdemo.DemoApp
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.support.MyLog
import java.lang.ref.WeakReference


abstract class BaseActivity : AppCompatActivity() {
    abstract fun getContentViewId() : Int
    abstract fun getToolBar() : Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())
        setSupportActionBar(getToolBar())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getToolBar().setNavigationOnClickListener{onBackPressed()}
        DemoApp.addActivity(WeakReference(this))
        MyLog("taskID："+taskId)
    }

    override fun onDestroy() {
        super.onDestroy()
        ApiProvider.unSubscribe(this)
    }
}
