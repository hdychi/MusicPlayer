package hdychi.hencoderdemo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class PlayMusicService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;
    private MyBinder binder = new MyBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mediaPlayer = MediaPlayer.create(this,R.raw.green_light);
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void playMusic(){
        isPlaying = !isPlaying;
        if(isPlaying){
            mediaPlayer.start();
        }
        else{
            mediaPlayer.pause();
        }
    }
    public void lastSong(){

    }
    public void nextSong(){

    }
    public void seekTime(int progress){
        mediaPlayer.seekTo((int)Math.floor(progress / 100.0 * mediaPlayer.getDuration()));
    }
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Bitmap getAlbum(){
       mediaPlayer.
    }
    public class MyBinder extends Binder {
        PlayMusicService getService(){
            return PlayMusicService.this;
        }
    }

}
