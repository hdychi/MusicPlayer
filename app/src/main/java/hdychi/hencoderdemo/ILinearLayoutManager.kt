package hdychi.hencoderdemo

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class ILinearLayoutManager(context : Context) : LinearLayoutManager(context){
    override fun canScrollVertically(): Boolean {
        return false
    }
}