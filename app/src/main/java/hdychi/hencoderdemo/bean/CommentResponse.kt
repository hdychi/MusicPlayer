package hdychi.hencoderdemo.bean

data class CommentResponse(val total: Int = 0,
                           val code: Int = 0,
                           val comments: List<CommentsItem>?,
                           val hotComments: List<CommentsItem>?,
                           val more: Boolean = false,
                           val userId: Int = 0,
                           val moreHot: Boolean = false,
                           val isMusician: Boolean = false)


data class User(val locationInfo: String? = null,
                val avatarUrl: String = "",
                val authStatus: Int = 0,
                val nickname: String = "",
                val vipType: Int = 0,
                val remarkName: String? = null,
                val userType: Int = 0,
                val userId: Int = 0)


data class CommentsItem(val commentId: Int = 0,
                        val likedCount: Int = 0,
                        val time: Long = 0,
                        val user: User,
                        val liked: Boolean = false,
                        val content: String = "")




