package hdychi.hencoderdemo.ui.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import hdychi.hencoderdemo.ui.activities.CommentActivity
import hdychi.hencoderdemo.CommonData
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.api.ApiProvider
import hdychi.hencoderdemo.bean.SongsItem
import hdychi.hencoderdemo.interfaces.OnFragmentClickListener
import hdychi.hencoderdemo.interfaces.OnPauseMusicListener
import rx.Subscriber

private const val ARG_PARAM1 = "id"


class AlbumFragment : Fragment(),OnPauseMusicListener{

    private var id: Int? = null
    private var listener: OnFragmentClickListener? = null
    private var animator : ObjectAnimator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val res = inflater.inflate(R.layout.fragment_album, container, false)
        val love_btn = res.findViewById<Button>(R.id.frag_love_btn)
        val cmt_btn = res.findViewById<Button>(R.id.frag_cmt_btn)
        val album_pic = res.findViewById<SimpleDraweeView>(R.id.frag_song_pic)
        album_pic.setOnClickListener { onAlbumPressed() }
        love_btn.setOnClickListener { onLovePressed() }
        cmt_btn.setOnClickListener { jump() }
        animator = ObjectAnimator.ofFloat(album_pic,"rotation",0.0f,360.0f)
                .setDuration(3000)
        animator?.interpolator = LinearInterpolator()
        animator?.repeatCount = -1
        animator?.start()
        animator?.pause()
        val subscriber = object : Subscriber<SongsItem>(){
            override fun onNext(t: SongsItem?) {
                album_pic.setImageURI(t?.al?.picUrl)
            }
            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                Toast.makeText(activity,"获取歌曲详情失败",
                        Toast.LENGTH_SHORT).show()
            }

        }
        ApiProvider.getSongDetail(subscriber,id?:0)
        return res
    }

    private fun onAlbumPressed() {
        listener?.onFragmentClick(CommonData.ALBUM_FRAGMENT_ID)
    }

    fun onLovePressed(){

    }

    override fun onPauseMusic(isPlaying : Boolean) {
        when (isPlaying) {
            true -> animator?.resume()
            false -> animator?.pause()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentClickListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun jump(){
        val intent  = Intent(activity, CommentActivity::class.java)
        intent.putExtra(ARG_PARAM1,id)
        startActivity(intent)
    }

    companion object {

        @JvmStatic
        fun newInstance(id : Int) =
                AlbumFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, id)
                    }
                }
    }
}
