package hdychi.hencoderdemo.support


import android.app.Notification
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
import android.app.NotificationChannel
import android.os.Build
import hdychi.hencoderdemo.ui.activities.MainActivity
import hdychi.hencoderdemo.ui.activities.PlayNetActivity


class NotificationProvider(context: Context){

    private var manager : NotificationManager
    private var builder : NotificationCompat.Builder? = null
    private var mRemoteViews : RemoteViews
    private var notify : Notification
    private var mContext : Context? = context
    private var notificationReceiver : NotificationReceiver
    init {
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mRemoteViews = RemoteViews(context.packageName, R.layout.custom_notification)
        if(Build.VERSION.SDK_INT >= 26){
            val chanel_id = "3000"
            val name = "Channel Name"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(chanel_id, name, importance)
            manager.createNotificationChannel(mChannel)
            builder = NotificationCompat.Builder(context, mChannel.id)
        }
        else{
            builder =  NotificationCompat.Builder(context)
        }
        notify = builder!!
                .setSmallIcon(R.drawable.play)
                .setCustomContentView(mRemoteViews)
                .setCustomBigContentView(mRemoteViews)
                .setOngoing(true)
                .build()
        val intentActivity = Intent(Intent.ACTION_MAIN)
        intentActivity.addCategory(Intent.CATEGORY_LAUNCHER)
        intentActivity.setClass(context,MainActivity::class.java)
        intentActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        val pendingIntentAct = PendingIntent.getActivity(context,CommonData.PLAY_ACTIVITY_REQUEST_CODE,intentActivity, 0)
        notify.contentIntent = pendingIntentAct
        val intentPre = Intent(CommonData.NOTIFICATION_ACTION)
        intentPre.putExtra(CommonData.NOTIFICATION_EXTRA,CommonData.PRE_REQUEST_CODE)
        val pendingIntentPre = PendingIntent.getBroadcast(context,CommonData.PRE_REQUEST_CODE,intentPre
                ,FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.widget_prev,pendingIntentPre)

        val intentPlay = Intent(CommonData.NOTIFICATION_ACTION)
        intentPlay.putExtra(CommonData.NOTIFICATION_EXTRA,CommonData.PLAY_PAUSE_REQUEST_CODE)
        val pendingIntentPlay = PendingIntent.getBroadcast(context,CommonData.PLAY_PAUSE_REQUEST_CODE,
                intentPlay, FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.widget_play,pendingIntentPlay)

        val intentNext = Intent(CommonData.NOTIFICATION_ACTION)
        intentNext.putExtra(CommonData.NOTIFICATION_EXTRA,CommonData.NEXT_REQUEST_CODE)
        val pendingIntentNext = PendingIntent.getBroadcast(context,CommonData.NEXT_REQUEST_CODE,intentNext,
                FLAG_UPDATE_CURRENT)
        mRemoteViews.setOnClickPendingIntent(R.id.widget_next,pendingIntentNext)

        val intentFilter = IntentFilter()
                .apply { addAction(CommonData.NOTIFICATION_ACTION) }
        notificationReceiver = NotificationReceiver()
        context.registerReceiver(notificationReceiver,intentFilter)

    }
    fun refresh(id : Int){
        val subscriber = CommonSubscriber<SongsItem>(mContext!!,"获取歌曲失败")
        subscriber.onSuccessController = OnSuccessController {
            t ->
            mRemoteViews.setTextViewText(R.id.notification_title,t.name)
            mRemoteViews.setTextViewText(R.id.notification_artist, MusicUtil.getArStr(t.ar))
            try {
                Glide.with(mContext!!)
                        .asBitmap()
                        .load(t.al?.picUrl)
                        .into(object : SimpleTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                mRemoteViews.setImageViewBitmap(R.id.notification_album,resource)
                                manager.notify(CommonData.NOTIFICATION_ID,notify)
                            }

                        })
            }
            catch (e : Exception){
                manager.notify(CommonData.NOTIFICATION_ID,notify)
                e.printStackTrace()
            }

        }
        ApiProvider.getSongDetail(this,subscriber,id)
    }
    fun destroy(){
        mContext?.unregisterReceiver(notificationReceiver)
        mContext = null
        manager.cancel(CommonData.NOTIFICATION_ID)
    }

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