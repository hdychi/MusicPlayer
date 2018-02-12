package hdychi.hencoderdemo.main

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.bean.PlaylistItem
import hdychi.hencoderdemo.interfaces.OnItemClickListener

class PlayListAdapter : RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>(){
    private val mItems = mutableListOf<PlaylistItem>()
    var onItemClickListener : OnItemClickListener?= null
    fun addAll(playlistItems: MutableList<PlaylistItem>){
        mItems.clear()
        mItems.addAll(playlistItems)
        notifyDataSetChanged()
    }
    fun get(index : Int) : PlaylistItem{
        return mItems[index]
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlayListViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.play_list_item,parent,false)
        return PlayListViewHolder(view)
    }

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: PlayListViewHolder?, position: Int) {
        val nowItem = mItems[position]
        holder?.pic?.setImageURI(Uri.parse(nowItem.coverImgUrl))
        holder?.title?.text = nowItem.name
        holder?.trackCount?.text = nowItem.trackCount.toString() + "首"
        holder?.mItemView?.setOnClickListener { onItemClickListener?.onClick(position) }
    }

    class PlayListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val pic : ImageView
        val title : TextView
        val trackCount : TextView
        val mItemView :View
        init {
            pic = itemView.findViewById(R.id.play_item_pic)
            title = itemView.findViewById(R.id.play_item_title)
            trackCount = itemView.findViewById(R.id.track_count)
            mItemView = itemView
        }
    }
}