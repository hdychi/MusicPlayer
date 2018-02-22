package hdychi.hencoderdemo.bean


import com.google.gson.annotations.SerializedName

data class MusicUrl(val code: Int = 0,
                    val expi: Int = 0,
                    val flag: Int = 0,
                    val fee: Int = 0,
                    val type: String = "",
                    val canExtend: Boolean = false,
                    val url: String = "",
                    val gain: Double = 0.0,
                    val br: Int = 0,
                    val size: Int = 0,
                    val id: Int = 0,
                    @SerializedName("md5")
                    val md: String = "",
                    val payed: Int = 0)


data class MusicUrlResponse(val code: Int = 0,
                            val data: List<MusicUrl>?)


