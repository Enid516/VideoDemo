package com.example.biglove_work.videodemo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by biglove-work on 2016/5/16.
 */
public class HttpVideoView extends Activity{

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pay_http_video);
        videoView = (VideoView) findViewById(R.id.video_view);

        String string  = "rtsp://v2.cache2.c.youtube.com/CjgLENy73wIaLwm3JbT_%ED%AF%80%ED%B0%819HqWohMYESARFEIJbXYtZ29vZ2xlSARSB3Jlc3VsdHNg_vSmsbeSyd5JDA==/0/0/0/video.3gp";
//        string = "rtsp://192.168.120.101/";
        Uri videoUri = Uri.parse(string);

        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(videoUri);
        videoView.start();
//        videoView.requestFocus();
    }
}
