package hdychi.hencoderdemo.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hdychi.hencoderdemo.CommonData

import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.interfaces.OnFragmentClickListener
import hdychi.hencoderdemo.interfaces.OnSeekToListener
import hdychi.hencoderdemo.lrc.LrcUtil
import hdychi.hencoderdemo.lrc.LrcView
import hdychi.hencoderdemo.support.toast
import kotlinx.android.synthetic.main.fragment_lyric_frament.*
import rx.Subscriber


private const val ARG_PARAM1 = "id"

class LyricFrament : Fragment() {

    private var id: Int? = null
    private var listener: OnFragmentClickListener? = null
    private var onSeekToListener : OnSeekToListener? = null
    var lrcView: LrcView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val res = inflater.inflate(R.layout.fragment_lyric_frament, container, false)
        lrcView = res.findViewById<LrcView>(R.id.lrc_view)
        val subscriber = object : Subscriber<String>(){
            override fun onNext(t: String?) {
                lrcView?.setLrcRows(LrcUtil.getLrcRows(t))
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                activity?.toast("获取歌词错误")
            }

        }
        ApiProvider.getLyric(subscriber,id?:0)
        lrcView?.setOnLrcClickListener { onLyricPressed() }
        lrcView?.setOnSeekToListener(onSeekToListener)
        return res
    }

    fun refresh(timeSecs : Int){
        lrcView?.seekTo(timeSecs,false)
    }
    private fun onLyricPressed() {
        listener?.onFragmentClick(CommonData.LYRIC_FRAGMENT_ID)
    }
    fun isDragging() : Boolean{
        return lrcView?.isDraging?:false
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentClickListener) {
            listener = context
        }
        if (context is OnSeekToListener){
            onSeekToListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
                LyricFrament().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, param1)
                    }
                }
    }
}
