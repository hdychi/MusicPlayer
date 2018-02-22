package hdychi.hencoderdemo.support

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.toast(text : String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}
fun Intent.isInTask(context: Context) : Boolean{
    val cmpName : ComponentName?= this.resolveActivity(context.packageManager)
    if(cmpName != null){
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfoList = am.getRunningTasks(10)  //获取从栈顶开始往下查找的10个activity
        for (taskInfo in taskInfoList) {
            if (taskInfo.baseActivity == cmpName) { // 说明它已经启动了
                return true
            }
        }

    }
    return false
}