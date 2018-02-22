package hdychi.hencoderdemo.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import hdychi.hencoderdemo.R
import hdychi.hencoderdemo.bean.CommentsItem
import jp.wasabeef.glide.transformations.CropCircleTransformation
import java.text.SimpleDateFormat

open class CommentAdapter(hasFooter : Boolean,context : Context)
    : RecyclerView.Adapter<CommentAdapter.IViewHoler>(){
    var mHasFooter = false
    val timeFormat = SimpleDateFormat("yyyy-MM-dd")
    var mContext : Context
    init {
        mHasFooter = hasFooter
        mContext = context
    }
    val mItems : MutableList<CommentsItem> = mutableListOf()
    val TYPE_FOOTER = 0
    val TYPE_NO_FOOTER = 1
    fun addAll(comments : List<CommentsItem>?){
        mItems.clear()
        mItems.addAll(comments?: listOf())
        notifyDataSetChanged()
    }
    fun appendAll(comments : List<CommentsItem>?){
        val sPos = mItems.size
        mItems.addAll(comments?: listOf())
        when(mItems.size > 0 && comments?.size?:0 > 0){
            true -> notifyItemRangeChanged(sPos,comments?.size?:0)
            false -> notifyDataSetChanged()
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IViewHoler
    = when(viewType){
        TYPE_NO_FOOTER -> CommentHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_item,parent,false))
        TYPE_FOOTER -> FooterViewHoler(LayoutInflater.from(parent.context)
                    .inflate(R.layout.comment_foorter,parent,false))
        else -> CommentHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_item,parent))
    }

    override fun getItemCount() = when(mHasFooter) {
        true -> mItems.size + 1
        false -> mItems.size
    }
    override fun getItemViewType(position: Int) = when(position == mItems.size
            && mHasFooter) {
        true -> TYPE_FOOTER
        false -> TYPE_NO_FOOTER
    }
    override fun onBindViewHolder(holder: IViewHoler, position: Int) {

        if(holder is CommentHolder){
            val nowItem = mItems[position]
            Glide.with(mContext).load(nowItem.user.avatarUrl)
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(holder.avatar)

            holder.nickname.text = nowItem.user.nickname
            holder.time.text = timeFormat.format(nowItem.time)
            holder.content.text = nowItem.content
        }

    }
    open class IViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class CommentHolder(itemView : View) : IViewHoler(itemView){
        val mItemView : View
        val avatar : ImageView
        val nickname : TextView
        val time : TextView
        val content : TextView
        init {
            mItemView = itemView
            avatar = itemView.findViewById(R.id.avatar)
            nickname = itemView.findViewById(R.id.nickname)
            time = itemView.findViewById(R.id.comment_time)
            content = itemView.findViewById(R.id.comment_content)
        }
    }
    inner class FooterViewHoler(itemView: View) : IViewHoler(itemView)
}

