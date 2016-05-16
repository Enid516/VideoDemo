package com.example.biglove_work.videodemo;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import java.io.IOException;

public class CustomVideoPlayer extends AppCompatActivity
        implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback ,MediaController.MediaPlayerControl{
    private static final String LOG_TAG = "CustomVideoPlayer";
    private Display currentDisplay;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private MediaPlayer mediaPlayer;
    private int videoWidth = 0, videoHeight = 0;
    private boolean redayToPlay = false;

    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_video_player);
        init();
    }

    private void init() {
        surfaceView = (SurfaceView) findViewById(R.id.act_custom_video_player_sv);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);//指定回调监听
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置缓冲区表面

        mediaPlayer = new MediaPlayer();//构造MediaPlayer对象
        setListener();//指定监听

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
        "/MIUI/Gallery/DemoVideo/XiaomiPhone.mp4";//播放文件
//        filePath = "rtsp://192.168.120.101/";
//        filePath = "rtsp://v2.cache2.c.youtube.com/CjgLENy73wIaLwm3JbT_%ED%AF%80%ED%B0%819HqWohMYESARFEIJbXYtZ29vZ2xlSARSB3Jlc3VsdHNg_vSmsbeSyd5JDA==/0/0/0/video.3gp";

        try {
            mediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentDisplay = getWindowManager().getDefaultDisplay();

        addMediaController();
    }

    private void setListener() {
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }

    private void addMediaController() {
        mediaController = new MediaController(this);

    }

    private void showMediaController() {
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.main_view));
        mediaController.setEnabled(true);
        mediaController.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //使控制器在消失之后重新显示
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            mediaController.show();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {//因为注册了setOnCompletionListener，视频播放完成时会调用onCompletion（）方法
        Log.v(LOG_TAG,"onCompletion Called");
        Toast.makeText(this,"播放完成",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {//当出现关于播放文件特定信息或者需要发出警告时将调用onInfo方法
        if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {

        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v(LOG_TAG,"onError Called");
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            Log.v(LOG_TAG,"Media Error: Server Died" + extra);
        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
            Log.v(LOG_TAG,"Media Error: Error Unknown" + extra);
        }
        return false;//return false 表示错误没有被处理，如果注册了OnCompletionListener，则会调用onCompletion方法
                        //同时将MediaPlayer设置为“错误”状态，可以通过调用reset方法，将它重置为空闲状态
    }

    @Override
    public void onPrepared(MediaPlayer mp) {//当文件成功的准备播放之后调用onPrepared
        Log.v(LOG_TAG, "onPrepared Called");

        //获取视频尺寸
        videoWidth = mp.getVideoWidth();
        videoHeight = mp.getVideoHeight();

        //如果视频高度或宽度大于显示器大小，需要找出应该使用的比率
        if (videoWidth > currentDisplay.getWidth() || videoHeight > currentDisplay.getHeight()) {
            float widthRatio = (float) videoWidth / (float)currentDisplay.getWidth();
            float heightRatio = (float) videoHeight / (float)currentDisplay.getHeight();
            if (widthRatio > 1 || heightRatio > 1) {
                //使用较大的比率,通过视频大小除以较大的比率来设置videoWidth,videoHeight
                if (widthRatio > heightRatio) {
                    videoWidth = (int) Math.ceil((float) videoWidth / (float) widthRatio);
                    videoHeight = (int) Math.ceil((float) videoHeight / (float) widthRatio);
                } else {
                    videoWidth = (int) Math.ceil((float) videoWidth / (float) heightRatio);
                    videoHeight = (int) Math.ceil((float) videoHeight / (float) heightRatio);
                }
            }
        }

        //设置surfaceView的大小
        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth,videoHeight));

        //开始播放视频
        mp.start();

        showMediaController();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.v(LOG_TAG, "onSeekComplete Called");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(LOG_TAG, "onVideoSizeChanged Called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {//当创建SurfaceView的底层表面时，会调用surfaceCreated()
        Log.v(LOG_TAG,"surfaceCreated Called");
        mediaPlayer.setDisplay(holder);//指定MediaPlayer将在该表面播放

        try {
            mediaPlayer.prepareAsync();
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOG_TAG,"surfaceChanged Called");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(LOG_TAG,"surfaceDestroyed Called");
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
