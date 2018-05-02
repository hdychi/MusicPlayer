package hdychi.hencoderdemo.support

import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.provider.Contacts
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.CommonSubscriber
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.SongsItem
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.interfaces.PlayController
import kotlinx.coroutines.experimental.launch

fun Context.toast(text : String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}
fun Activity.toast(text: String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}
fun MyLog(text: String){
    Log.i("测试",text)
}
fun Intent.isInTask(context: Context) : Boolean{
    val cmpName : ComponentName?= this.resolveActivity(context.packageManager)
    if(cmpName != null){
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfoList = am.getRunningTasks(10)  //获取从栈顶开始往下查找的10个activity
        for (taskInfo in taskInfoList) {
            if (taskInfo.baseActivity == cmpName) { // 说明它已经启动了
                return true
            }
        }

    }
    return false
}
fun Context.showNotification(id : Int){

    val manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder =  NotificationCompat.Builder(this)
    val mRemoteViews = RemoteViews(applicationContext.packageName, R.layout.custom_notification)
    builder.setSmallIcon(R.drawable.play)
    val subscriber = CommonSubscriber<SongsItem>(this,"获取歌曲失败")
    subscriber.onSuccessController = OnSuccessController {
        t ->
        Glide.with(this)
                .asBitmap()
                .load(t.al?.picUrl)
                .into(object : SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        mRemoteViews.setImageViewBitmap(R.id.notification_album,resource)
                        mRemoteViews.setTextViewText(R.id.notification_title,t.name)
                        mRemoteViews.setTextViewText(R.id.notification_artist,MusicUtil.getArStr(t.ar))
                        val notify = builder
                                .setLargeIcon(null)
                                .setContent(mRemoteViews)
                                .setCustomBigContentView(mRemoteViews)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setOngoing(true)
                                .build()

                        manager.notify(CommonData.NOTIFICATION_ID,notify)
                        MyLog("资源准备好了")
                    }

                })



    }
    if(this is PlayController){

    }
    ApiProvider.getSongDetail(this,subscriber,id)
}