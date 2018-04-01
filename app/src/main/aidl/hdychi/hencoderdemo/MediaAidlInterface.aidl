package hdychi.hencoderdemo;


interface MediaAidlInterface {
  void playOrPause();
  void prev();
  void next();
  int duration();
  int postion();
  void seekSec(int secs);
  void seekProgress(float progress);
  void reset();
  boolean isPlaying();
  String getInfo();
}