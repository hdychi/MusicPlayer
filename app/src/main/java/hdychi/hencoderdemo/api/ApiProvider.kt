package hdychi.hencoderdemo.api

import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.bean.PlaylistItem
import hdychi.hencoderdemo.bean.Result
import hdychi.hencoderdemo.bean.UserBean
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.INFO
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Logger
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class ApiProvider(){
    val loggingInterceptor = HttpLoggingInterceptor { message ->
        if (message.startsWith("{")) {
            Logger.DEFAULT.log(message)
        } else {
            Platform.get().log(INFO, message, null)
        }
    }
    val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(30,TimeUnit.SECONDS)
            .build()

    val mRetrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.102:3000/")
            .client(client)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val mService = mRetrofit.create(Api::class.java)

    fun getUser(subscriber: Subscriber<UserBean>,userName : String,pwd : String){
        mService.login(userName,pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }

    fun getPlayLists(subscriber: Subscriber<MutableList<PlaylistItem>>){
        mService.getPlayLists(CommonData.user.profile!!.userId)
                .map { t -> t.playlist as MutableList}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }

    fun getSongs(subscriber: Subscriber<Result>,id : Int){
        mService.getListDetail(id)
                .filter { t -> t.code / 100 == 2|| t.code / 100 ==3 }
                .map { t -> t.result }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber)
    }
}