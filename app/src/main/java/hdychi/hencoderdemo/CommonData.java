package hdychi.hencoderdemo;

import java.util.ArrayList;
import java.util.List;

import hdychi.hencoderdemo.bean.Mp3Info;
import hdychi.hencoderdemo.bean.TracksItem;
import hdychi.hencoderdemo.bean.UserBean;

public class CommonData {
    public static final int LOCAL = 0;
    public static final int NET = 1;
    public static UserBean user;
    public static List<Mp3Info> localMusicList = new ArrayList<>();
    public static List<TracksItem> netMusicList = new ArrayList<>();
    public static int mode = NET;
}
