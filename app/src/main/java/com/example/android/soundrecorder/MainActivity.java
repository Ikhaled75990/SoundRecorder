package com.example.android.soundrecorder;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.transition.Fade.OUT;


public class MainActivity extends AppCompatActivity {



    MediaRecorder mMediaRecorder;
    MediaPlayer mMediaPlayer;
    private String OUTPUT_FILE;
    private Button record, stop, playBackToday, playBackYesterday;
    //SimpleDateFormat currentDateTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) MainActivity.super.finish();
        if (!permissionToWriteAccepted ) MainActivity.super.finish();

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        record = (Button) findViewById(R.id.record_audio);
        stop = (Button) findViewById(R.id.stop_recording);
        playBackToday = (Button) findViewById(R.id.playback_today);
        playBackYesterday = (Button) findViewById(R.id.playback_yesterday);

        stop.setEnabled(false);
        playBackToday.setEnabled(false);
        playBackYesterday.setEnabled(false);

        OUTPUT_FILE = Environment.getExternalStorageDirectory() + "/" + currentDateTimeString + ".3gpp";
        mMediaRecorder = new MediaRecorder();
        mMediaPlayer = new MediaPlayer();


    }


    public void tappedButton(View view) {
        switch (view.getId()) {
            case R.id.record_audio:
                try {
                    startRecording();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop_recording:

                try {
                    stopRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case R.id.playback_today:
                try {
                    todayRecording();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.playback_yesterday:
                try {
                    //Method to play yesterday's recording
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private void startRecording() throws Exception {


       File outFile = new File(OUTPUT_FILE);
       if (outFile.exists())
           outFile.delete();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(OUTPUT_FILE);
        mMediaRecorder.setMaxDuration(60000);
        mMediaRecorder.prepare();
        mMediaRecorder.start();

        record.setEnabled(false);
        stop.setEnabled(true);


        Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show();



    }

    private void stopRecording() {

        try {
            mMediaRecorder.stop();
        } catch(RuntimeException e) {

        } finally {
            stop.setEnabled(false);
            playBackToday.setEnabled(true);
            playBackYesterday.setEnabled(true);

        }
        Toast.makeText(this, "Recording stopped!", Toast.LENGTH_SHORT).show();
    }

    private void releaseRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }

    }

    private void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    private void todayRecording() throws IOException {
        releasePlayer();
        mMediaPlayer.setDataSource(OUTPUT_FILE);
        mMediaPlayer.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp){
                mMediaPlayer.start();
            }
        });
        mMediaPlayer.prepareAsync();
       // mMediaPlayer.prepare();
        mMediaPlayer.start();

        Toast.makeText(this, "Playback successful!", Toast.LENGTH_SHORT).show();


    }






}
