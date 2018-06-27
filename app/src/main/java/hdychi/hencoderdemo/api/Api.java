package hdychi.hencoderdemo.api;

import hdychi.hencoderdemo.bean.CommentResponse;
import hdychi.hencoderdemo.bean.LyricResponse;
import hdychi.hencoderdemo.bean.MusicUrlResponse;
import hdychi.hencoderdemo.bean.PlayDetailResponse;
import hdychi.hencoderdemo.bean.PlayListResponse;
import hdychi.hencoderdemo.bean.SongDetailResponse;
import hdychi.hencoderdemo.bean.UserBean;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {
    @GET("login/cellphone")
    Observable<UserBean> login(@Query("phone") String phone,@Query("password") String pwd);
    @GET("user/playlist")
    Observable<PlayListResponse> getPlayLists(@Query("uid") Long uid);
    @GET("playlist/detail")
    Observable<PlayDetailResponse> getListDetail(@Query("id") Long id);
    @GET("music/url")
    Observable<MusicUrlResponse> getMusicUrl(@Query("id")int id);
    @GET("song/detail")
    Observable<SongDetailResponse> getSongDetail(@Query("ids")int id);
    @GET("comment/music")
    Observable<CommentResponse> getSongComment(@Query("id") int id,@Query("limit") int limit,@Query("offset")int offset);
    @GET("lyric")
    Observable<LyricResponse> getLyric(@Query("id") int id);
}
