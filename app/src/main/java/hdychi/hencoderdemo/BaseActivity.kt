package hdychi.hencoderdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import hdychi.hencoderdemo.R.id.toolbar



abstract class BaseActivity : AppCompatActivity() {
    abstract fun getContentViewId() : Int
    abstract fun getToolBar() : Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())
        setSupportActionBar(getToolBar())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getToolBar()?.setNavigationOnClickListener{onBackPressed()}
    }
}
