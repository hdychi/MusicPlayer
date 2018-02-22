package hdychi.hencoderdemo.bean

data class Lyric(val lyric: String = "",
                  val version: Int = 0)

data class Lrc(val lyric: String = "",
               val version: Int = 0)


data class LyricResponse(val code: Int = 0,
                         val qfy: Boolean = false,
                         val klyric: Lyric?,
                         val sfy: Boolean = false,
                         val tlyric: Lyric?,
                         val lrc: Lrc?,
                         val sgc: Boolean = false)


