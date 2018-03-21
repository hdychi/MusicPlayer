package hdychi.hencoderdemo

import android.content.Context
import hdychi.hencoderdemo.interfaces.OnEndController
import hdychi.hencoderdemo.interfaces.OnErrorController
import hdychi.hencoderdemo.interfaces.OnSuccessController
import hdychi.hencoderdemo.support.toast
import rx.Subscriber

class CommonSubscriber<T>(context: Context) : Subscriber<T>(){
    var errorMessage : String = "遇到错误"
    var onSuccessController : OnSuccessController<T>? = null
    var onErrorController : OnErrorController? = null
    var onEndController : OnEndController? = null
    var mContext : Context?= null
    init {
        mContext = context
    }
    constructor(context: Context,errorMsg : String) : this(context){
        errorMessage = errorMsg
    }
    override fun onCompleted() {}
    override fun onNext(t: T) {
        onSuccessController?.onSuccess(t)
        onEndController?.onEnd()
    }

    override fun onError(e: Throwable?) {
        e?.printStackTrace()
        mContext?.toast(errorMessage)
        onErrorController?.onFail(e)
        onEndController?.onEnd()
    }
}