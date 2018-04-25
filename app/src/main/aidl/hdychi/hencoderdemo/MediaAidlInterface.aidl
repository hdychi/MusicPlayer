package hdychi.hencoderdemo;


interface MediaAidlInterface {
  void playOrPause();
  void prev(int index);
  void next(int index);
  int duration();
  int postion();
  void seekSec(int secs);
  void seekProgress(float progress);
  void reset();
  boolean isPlaying();
  String getInfo();
  void setPlayList(in List<String> playList);
  void setNowindex(int index);
  int getPlayingId();
}