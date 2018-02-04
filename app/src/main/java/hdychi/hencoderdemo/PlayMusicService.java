package hdychi.hencoderdemo;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.util.List;


public class PlayMusicService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;
    private List<Mp3Info> musicList;
    private int nowIndex = 0;
    private OnChangeListener onChangeListener;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        musicList = MusicUtil.INSTANCE.getMusicList();
        resetPlayer();
        return new MyBinder();
    }
    public void resetPlayer(){
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(this,
                Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        +"/"+ getCurrentSong().getId()));
        isPlaying = false;
        if(onChangeListener != null){
            onChangeListener.onChange();
        }
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
    public void lastSong() {
        nowIndex--;
        if(nowIndex < 0){
            nowIndex = musicList.size() - 1;
        }
        resetPlayer();
    }
    public void nextSong(){
        nowIndex++;
        if(nowIndex > musicList.size()){
            nowIndex = 0;
        }
        resetPlayer();

    }
    public void seekTime(int progress){
        mediaPlayer.seekTo((int)Math.floor(progress / 100.0 * mediaPlayer.getDuration()));
    }
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public Bitmap getPic(){
        return MusicUtil.INSTANCE.getArtwork(this,
                getCurrentSong().getId(),
                getCurrentSong().getAlbumId(),true,true);
    }
    public Mp3Info getCurrentSong(){
        return musicList.get(nowIndex);
    }
    public class MyBinder extends Binder {
        PlayMusicService getService(OnChangeListener listener){
            PlayMusicService.this.onChangeListener = listener;
            return PlayMusicService.this;
        }
    }

}
