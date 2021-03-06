package hdychi.hencoderdemo.api

import android.util.Log
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.bean.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.INFO
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Logger
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import rx.subscriptions.CompositeSubscription




object ApiProvider{

    val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .connectTimeout(10,TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

    val mRetrofit = Retrofit.Builder()
            .baseUrl("http://172.18.228.42:3000/")
            .client(client)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val mService = mRetrofit.create(Api::class.java)
    val mSubscriptionsMap = mutableMapOf<Any, CompositeSubscription>()


    fun getUser(tag : Any,subscriber: Subscriber<UserBean>,userName : String,pwd : String){
        mService.login(userName,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }

    fun getPlayLists(tag : Any,subscriber: Subscriber<MutableList<PlaylistItem>>){
        mService.getPlayLists(CommonData.getUser()?.profile?.userId)
                .filter{t -> t != null}
                .map { t -> t.playlist as MutableList}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }

    fun getSongs(tag : Any, subscriber: Subscriber<Result>,id : Long){
        mService.getListDetail(id)
                .filter { t -> t.code / 100 == 2|| t.code / 100 ==3 }
                .map { t -> t.playlist }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }
    fun getMusicUrl(tag : Any,subscriber: Subscriber<String>,id :Int){
        mService.getMusicUrl(id)
                .map { t -> t.data!!.first().url }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }
    fun getSongDetail(tag : Any,subscriber: Subscriber<SongsItem>,id : Int){
        mService.getSongDetail(id)
                .map { t -> t.songs!!.first() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }
    fun getSongComment(tag : Any,subscriber : Subscriber<CommentResponse>,id : Int,limit : Int,offset : Int){
        mService.getSongComment(id,limit,offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }
    fun getLyric(tag : Any,subscriber: Subscriber<String>,id : Int){
        mService.getLyric(id)
                .map { t -> t.lrc!!.lyric }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)

    }
}