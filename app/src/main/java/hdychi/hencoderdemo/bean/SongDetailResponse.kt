package hdychi.hencoderdemo.bean


import com.google.gson.annotations.SerializedName

data class ArItem(val name: String = "",
                  val id: Int = 0)


data class PrivilegesItem(val st: Int = 0,
                          val flag: Int = 0,
                          val subp: Int = 0,
                          val fl: Int = 0,
                          val fee: Int = 0,
                          val dl: Int = 0,
                          val cp: Int = 0,
                          val cs: Boolean = false,
                          val toast: Boolean = false,
                          val maxbr: Int = 0,
                          val id: Int = 0,
                          val pl: Int = 0,
                          val sp: Int = 0,
                          val payed: Int = 0)



data class Al(val picUrl: String = "",
              val name: String = "",
              val id: Int = 0,
              val pic: Long = 0)


data class SongsItem(val no: Int = 0,
                     val rt: String = "",
                     val copyright: Int = 0,
                     val fee: Int = 0,
                     val rurl: String? = null,
                     val mst: Int = 0,
                     val pst: Int = 0,
                     val pop: Int = 0,
                     val dt: Int = 0,
                     val rtype: Int = 0,
                     @SerializedName("s_id")
                     val sId: Int = 0,
                     val id: Int = 0,
                     val st: Int = 0,
                     val a: String? = null,
                     val cd: String = "",
                     val publishTime: Long = 0,
                     val cf: String = "",
                     val mv: Int = 0,
                     val al: Al?,
                     val cp: Int = 0,
                     val djId: Int = 0,
                     val crbt: String? = null,
                     val ar: List<ArItem>?,
                     val rtUrl: String? = null,
                     val ftype: Int = 0,
                     val t: Int = 0,
                     val v: Int = 0,
                     val name: String = "")




data class SongDetailResponse(val privileges: List<PrivilegesItem>?,
                              val code: Int = 0,
                              val songs: List<SongsItem>?)


