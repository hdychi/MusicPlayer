package hdychi.hencoderdemo.bean


import com.google.gson.annotations.SerializedName

data class PlayListResponse(val code: Int = 0,
                            val playlist: List<PlaylistItem>?,
                            val more: Boolean = false)


data class PlaylistItem(val privacy: Int = 0,
                        val description: String? = null,
                        val trackNumberUpdateTime: Long = 0,
                        val subscribed: Boolean = false,
                        val trackCount: Int = 0,
                        val adType: Int = 0,
                        @SerializedName("coverImgId_str")
                        val coverImgIdStr: String = "",
                        val specialType: Int = 0,
                        val id: Long = 0,
                        val totalDuration: Int = 0,
                        val ordered: Boolean = false,
                        val creator: Profile?,
                        val highQuality: Boolean = false,
                        val commentThreadId: String = "",
                        val updateTime: Long = 0,
                        val trackUpdateTime: Long = 0,
                        val userId: Int = 0,
                        val anonimous: Boolean = false,
                        val coverImgUrl: String = "",
                        val cloudTrackCount: Int = 0,
                        val playCount: Int = 0,
                        val coverImgId: Long = 0,
                        val createTime: Long = 0,
                        val name: String = "",
                        val subscribedCount: Int = 0,
                        val status: Int = 0,
                        val newImported: Boolean = false)


