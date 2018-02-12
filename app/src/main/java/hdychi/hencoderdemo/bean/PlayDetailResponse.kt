package hdychi.hencoderdemo.bean


import com.google.gson.annotations.SerializedName




data class Artist(val picUrl: String = "",
                  @SerializedName("img1v1Url")
                  val imgVUrl: String = "",
                  val briefDesc: String = "",
                  val musicSize: Int = 0,
                  val name: String = "",
                  @SerializedName("img1v1Id")
                  val imgVId: Int = 0,
                  val id: Int = 0,
                  val picId: Int = 0,
                  val albumSize: Int = 0,
                  val trans: String = "")


data class TracksItem(val no: Int = 0,
                      val copyright: Int = 0,
                      val dayPlays: Int = 0,
                      val fee: Int = 0,
                      val mMusic: Music?,
                      val bMusic: Music?,
                      val duration: Int = 0,
                      val score: Int = 0,
                      val rtype: Int = 0,
                      val starred: Boolean = false,
                      val artists: List<Artist>?,
                      val popularity: Int = 0,
                      val playedNum: Int = 0,
                      val hearTime: Int = 0,
                      val starredNum: Int = 0,
                      val id: Int = 0,
                      val mp3Url: String? = null,
                      val album: Album?,
                      val lMusic: Music?,
                      val ringtone: String = "",
                      val commentThreadId: String = "",
                      val copyFrom: String = "",
                      val ftype: Int = 0,
                      val copyrightId: Int = 0,
                      val hMusic: Music?,
                      val mvid: Int = 0,
                      val name: String = "",
                      val disc: String = "",
                      val position: Int = 0,
                      val status: Int = 0)


data class Music(@SerializedName("dfsId_str")
                  val dfsIdStr: String? = null,
                  val extension: String = "",
                  val size: Int = 0,
                  val volumeDelta: Double = 0.0,
                  val name: String? = null,
                  val bitrate: Int = 0,
                  val playTime: Int = 0,
                  val id: Int = 0,
                  val dfsId: Int = 0,
                  val sr: Int = 0)




data class Album(val publishTime: Long = 0,
                 @SerializedName("picId_str")
                 val picIdStr: String = "",
                 val artist: Artist?,
                 val blurPicUrl: String = "",
                 val description: String = "",
                 val commentThreadId: String = "",
                 val pic: Long = 0,
                 val type: String = "",
                 val tags: String = "",
                 val picUrl: String = "",
                 val companyId: Int = 0,
                 val size: Int = 0,
                 val briefDesc: String = "",
                 val copyrightId: Int = 0,
                 val artists: List<Artist>?,
                 val transNames: List<String>?,
                 val name: String = "",
                 val company: String = "",
                 val subType: String = "",
                 val id: Int = 0,
                 val picId: Long = 0,
                 val status: Int = 0)


data class PlayDetailResponse(val result: Result?,
                              val code: Int = 0)



data class Result(val privacy: Int = 0,
                  val description: String? = null,
                  val trackNumberUpdateTime: Long = 0,
                  val subscribed: Boolean = false,
                  val shareCount: Int = 0,
                  val adType: Int = 0,
                  val trackCount: Int = 0,
                  @SerializedName("coverImgId_str")
                  val coverImgIdStr: String = "",
                  val specialType: Int = 0,
                  val id: Int = 0,
                  val totalDuration: Int = 0,
                  val ordered: Boolean = false,
                  val creator: Profile?,
                  val highQuality: Boolean = false,
                  val commentThreadId: String = "",
                  val updateTime: Long = 0,
                  val trackUpdateTime: Long = 0,
                  val userId: Int = 0,
                  val tracks: List<TracksItem>?,
                  val anonimous: Boolean = false,
                  val commentCount: Int = 0,
                  val cloudTrackCount: Int = 0,
                  val coverImgUrl: String = "",
                  val playCount: Int = 0,
                  val coverImgId: Long = 0,
                  val createTime: Long = 0,
                  val name: String = "",
                  val subscribedCount: Int = 0,
                  val status: Int = 0,
                  val newImported: Boolean = false)


