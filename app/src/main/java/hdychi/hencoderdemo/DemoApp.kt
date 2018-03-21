package hdychi.hencoderdemo

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.orhanobut.hawk.Hawk
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.LeakCanary


class DemoApp : Application(){

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {//1
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this);
        Hawk.init(this).build()
        val config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build()
        Fresco.initialize(this,config)
        CommonData.context = this
    }
    companion object {
        val lists = mutableListOf<AppCompatActivity>()

        fun addActivity(activity: AppCompatActivity) {
            lists.add(activity)
        }

        fun clearActivity() {
            for (activity in lists) {
                activity.finish()
            }

            lists.clear()
        }
    }


}