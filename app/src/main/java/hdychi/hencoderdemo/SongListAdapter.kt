package hdychi.hencoderdemo

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hdychi.hencoderdemo.bean.TracksItem
import hdychi.hencoderdemo.interfaces.OnItemClickListener
import kotlin.math.min

class SongListAdapter : RecyclerView.Adapter<SongListAdapter.SongListVH>(){
    val mItems = mutableListOf<TracksItem>()
    var onItemClickListener : OnItemClickListener?= null
    fun addAll(playlistItems: MutableList<TracksItem>){
        mItems.clear()
        mItems.addAll(playlistItems)
        notifyDataSetChanged()
    }
    fun getItem(index : Int) : TracksItem{
        return mItems[index]
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongListVH {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.song_list_item,parent,false)
        Log.i("列表加载","执行1")
        return SongListVH(view)
    }

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: SongListVH?, position: Int) {
        val nowItem = mItems[position]
        holder?.songOrder?.text = (position + 1).toString()
        holder?.title?.text = nowItem.name
        var singers = MusicUtil.getArtistsStr(nowItem.artists)
        singers += "-" + nowItem.album?.name
        if(singers.length>17){
            singers = singers.substring(0, 17)
            singers += "..."
        }
        holder?.artist?.text = singers
        Log.i("列表加载","执行")
        holder?.mItemView?.setOnClickListener { onItemClickListener?.onClick(position) }
    }

    class SongListVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val mItemView : View
        val songOrder : TextView
        val title : TextView
        val artist : TextView
        init {
            this.mItemView = itemView
            songOrder = itemView.findViewById(R.id.song_order)
            title = itemView.findViewById(R.id.song_name)
            artist = itemView.findViewById(R.id.song_singer)
        }
    }
}