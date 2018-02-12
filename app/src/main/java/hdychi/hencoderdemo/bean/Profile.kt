package hdychi.hencoderdemo.bean


import com.google.gson.annotations.SerializedName




data class Profile(val birthday: Long = 0,
                   val backgroundUrl: String = "",
                   val detailDescription: String = "",
                   val gender: Int = 0,
                   val city: Int = 0,
                   val signature: String = "",
                   val description: String = "",
                   val accountStatus: Int = 0,
                   val avatarImgId: Long = 0,
                   val defaultAvatar: Boolean = false,
                   val backgroundImgIdStr: String = "",
                   val province: Int = 0,
                   val nickname: String = "",
                   val djStatus: Int = 0,
                   val avatarUrl: String = "",
                   val authStatus: Int = 0,
                   val vipType: Int = 0,
                   val userId: Long = 0,
                   val followed: Boolean = false,
                   val mutual: Boolean = false,
                   val avatarImgIdStr: String = "",
                   val authority: Int = 0,
                   val backgroundImgId: Long = 0,
                   val userType: Int = 0)


