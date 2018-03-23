package hdychi.hencoderdemo;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import hdychi.hencoderdemo.bean.Mp3Info;
import hdychi.hencoderdemo.bean.TracksItem;
import hdychi.hencoderdemo.bean.UserBean;

public class CommonData {
    public static Context context;
    public static int ALBUM_FRAGMENT_ID = 0;
    public static int LYRIC_FRAGMENT_ID = 1;
    private static UserBean user;
    private static List<Mp3Info> localMusicList = new ArrayList<>();
    private static List<TracksItem> netMusicList = new ArrayList<>();
    private static int nowIndex = 0;

    public static int getNetNowItemID(){
        return netMusicList.get(nowIndex).getId();
    }
    public static UserBean getUser() {
        user = Hawk.get("user");
        return user;
    }

    public static void setUser(UserBean user) {
        CommonData.user = user;
        Hawk.put("user",user);
    }

    public static List<Mp3Info> getLocalMusicList() {
        return localMusicList;
    }

    public static void setLocalMusicList(List<Mp3Info> localMusicList) {
        CommonData.localMusicList = localMusicList;
    }

    public static List<TracksItem> getNetMusicList() {
        return netMusicList;
    }

    public static void setNetMusicList(List<TracksItem> netMusicList) {
        CommonData.netMusicList = netMusicList;
    }

    public static int getNowIndex() {
        return nowIndex;
    }

    public static void setNowIndex(int nowIndex) {
        CommonData.nowIndex = nowIndex;
    }

    public static boolean isLogin() {
        return Hawk.get("login",false);
    }

    public static void setLogin(boolean isLogin) {
        Hawk.put("login",isLogin);
    }
}
