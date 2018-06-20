package hdychi.hencoderdemo.support

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
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

fun Context.showNotification(id : Int){

    val manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder =  NotificationCompat.Builder(this)
    val mRemoteViews = RemoteViews(this.packageName, R.layout.custom_notification)
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
                        mRemoteViews.setTextViewText(R.id.notification_artist, MusicUtil.getArStr(t.ar))
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
    val intentPre = Intent(CommonData.NOTIFICATION_ACTION)
    intentPre.putExtra(CommonData.NOTIFICATION_EXTRA,CommonData.PRE_REQUEST_CODE)
    val pendingIntentPre = PendingIntent.getBroadcast(this,CommonData.PRE_REQUEST_CODE,intentPre
            ,FLAG_UPDATE_CURRENT)
    mRemoteViews.setOnClickPendingIntent(R.id.widget_prev,pendingIntentPre)

    val intentPlay = Intent(CommonData.NOTIFICATION_ACTION)
    intentPlay.putExtra(CommonData.NOTIFICATION_EXTRA,CommonData.PLAY_PAUSE_REQUEST_CODE)
    val pendingIntentPlay = PendingIntent.getBroadcast(this,CommonData.PLAY_PAUSE_REQUEST_CODE,
            intentPlay, FLAG_UPDATE_CURRENT)
    mRemoteViews.setOnClickPendingIntent(R.id.widget_play,pendingIntentPlay)

    val intentNext = Intent(CommonData.NOTIFICATION_ACTION)
    intentNext.putExtra(CommonData.NOTIFICATION_EXTRA,CommonData.NEXT_REQUEST_CODE)
    val pendingIntentNext = PendingIntent.getBroadcast(this,CommonData.PRE_REQUEST_CODE,intentNext,
            FLAG_UPDATE_CURRENT)
    mRemoteViews.setOnClickPendingIntent(R.id.widget_next,pendingIntentNext)

    val intentFilter = IntentFilter()
            .apply { addAction(CommonData.NOTIFICATION_ACTION) }
    registerReceiver(NotificationReceiver(),intentFilter)
    ApiProvider.getSongDetail(this,subscriber,id)
}
class NotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getIntExtra(CommonData.NOTIFICATION_EXTRA,-1)
        if(eventId >= 0 && (context is PlayController)){
            val contextController = context as PlayController
            when(eventId){
                CommonData.PRE_REQUEST_CODE -> contextController.pre()
                CommonData.PLAY_PAUSE_REQUEST_CODE -> contextController.playOrPause()
                CommonData.NEXT_REQUEST_CODE -> contextController.next()
            }
        }
    }

}