package hdychi.hencoderdemo

import android.app.Application
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.orhanobut.hawk.Hawk
import android.support.v7.app.AppCompatActivity
import com.squareup.leakcanary.LeakCanary
import java.lang.ref.WeakReference


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
    //TODO:修复静态持有Acvitiy导致的内存泄漏问题（弱引用？）
    companion object {
        val lists = mutableListOf<WeakReference<AppCompatActivity>>()

        fun addActivity(activity: WeakReference<AppCompatActivity>) {
            lists.add(activity)
        }

        fun clearActivity() {
            for (activity in lists) {
                activity.get()?.finish()
            }

            lists.clear()
        }
    }


}