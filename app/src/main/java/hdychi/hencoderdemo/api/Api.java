package hdychi.hencoderdemo.api;

import hdychi.hencoderdemo.bean.PlayDetailResponse;
import hdychi.hencoderdemo.bean.PlayListResponse;
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
    Observable<PlayDetailResponse> getListDetail(@Query("id") int id);
}
