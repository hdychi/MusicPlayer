package hdychi.hencoderdemo

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.text.SimpleDateFormat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.ContentUris
import hdychi.hencoderdemo.bean.Artist
import hdychi.hencoderdemo.bean.Mp3Info
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


object MusicUtil{
    val LOCAL_MUSIC = 0
    val NETWROK_MUSIC = 1
    var mode = 1
    val albumArtUri = Uri.parse("content://media/external/audio/albumart")
    private val time = SimpleDateFormat("mm:ss")

    fun getMp3Infos(context:Context) : MutableList<Mp3Info>{
        val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
        val res : MutableList<Mp3Info> = mutableListOf()
        cursor.moveToFirst()
        while (!cursor.isAfterLast){

            val id =  cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID))
            val title = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)) // 音乐标题
            val artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST)) // 艺术家
            val album = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM)) //专辑
            val albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()
            val duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION)) // 时长
            val size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE)) // 文件大小
            val url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA)) // 文件路径
            val mp3Info = Mp3Info(id, title, artist, album, albumId,
                    duration, size, url)
            res.add(mp3Info)
            cursor.moveToNext()
        }
        return res
    }



    /**
     * 获取默认专辑图片
     * @param context
     * @return
     */
    fun getDefaultArtwork(context: Context): Bitmap {
        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.RGB_565

        return BitmapFactory.decodeStream(context.resources.openRawResource(R.raw.default_pic), null, opts)
    }


    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private fun getArtworkFromFile(context: Context, songid: Long,
                                   albumid: Long): Bitmap? {
        var bm: Bitmap? = null
        if (albumid < 0 && songid < 0) {
            throw IllegalArgumentException("Must specify an album or a song id")
        }
        try {
            val options = BitmapFactory.Options()
            var fd: FileDescriptor? = null
            if (albumid < 0) {
                val uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart")
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            } else {
                val uri = ContentUris.withAppendedId(albumArtUri, albumid)
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            }
            options.inSampleSize = 1
            // 只进行大小判断
            options.inJustDecodeBounds = true
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options)
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return bm
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    fun getArtwork(context: Context, song_id: Long, album_id: Long,
                   allowdefalut: Boolean, small: Boolean): Bitmap? {
        if (album_id < 0) {
            if (song_id < 0) {
                val bm = getArtworkFromFile(context, song_id, -1)
                if (bm != null) {
                    return bm
                }
            }
            return if (allowdefalut) {
                getDefaultArtwork(context)
            } else null
        }
        val res = context.contentResolver
        val uri = ContentUris.withAppendedId(albumArtUri, album_id)
        if (uri != null) {
            var cin: InputStream? = null
            try {
                cin = res.openInputStream(uri)
                val options = BitmapFactory.Options()
                //先制定原始大小
                options.inSampleSize = 1
                //只进行大小判断
                options.inJustDecodeBounds = true
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(cin, null, options)
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例  */
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合  */
                if (small) {
                    options.inSampleSize = computeSampleSize(options, 40)
                } else {
                    options.inSampleSize = computeSampleSize(options, 600)
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false
                options.inDither = false
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                cin = res.openInputStream(uri)
                return BitmapFactory.decodeStream(cin, null, options)
            } catch (e: FileNotFoundException) {
                var bm = getArtworkFromFile(context, song_id, album_id)
                if (bm != null) {
                    if (bm.config == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false)
                        if (bm == null && allowdefalut) {
                            return getDefaultArtwork(context)
                        }
                    }
                } else if (allowdefalut) {
                    bm = getDefaultArtwork(context)
                }
                return bm
            } finally {
                try {
                    if (cin != null) {
                        cin!!.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }

    /**
     * 对图片进行合适的缩放
     * @param options
     * @param target
     * @return
     */
    fun computeSampleSize(options: BitmapFactory.Options, target: Int): Int {
        val w = options.outWidth
        val h = options.outHeight
        val candidateW = w / target
        val candidateH = h / target
        var candidate = Math.max(candidateW, candidateH)
        if (candidate == 0) {
            return 1
        }
        if (candidate > 1) {
            if (w > target && w / candidate < target) {
                candidate -= 1
            }
        }
        if (candidate > 1) {
            if (h > target && h / candidate < target) {
                candidate -= 1
            }
        }
        return candidate
    }

    fun getArtistsStr(artists : List<Artist>?) : String{
        var res = ""
        artists?.forEach { t -> res += t.name + "、" }
        if (res.isNotEmpty()){
            res = res.substring(0,res.length - 1)
        }
        return res
    }
}