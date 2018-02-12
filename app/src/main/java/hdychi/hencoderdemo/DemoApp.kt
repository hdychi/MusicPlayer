package hdychi.hencoderdemo

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.fresco.FrescoImageLoader
import com.orhanobut.hawk.Hawk

class DemoApp : Application(){
    override fun onCreate() {
        super.onCreate()
        Hawk.init(this).build()
        Fresco.initialize(this)
    }
}