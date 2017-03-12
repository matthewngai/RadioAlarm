package com.example.matthew.morningvibe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Matthew on 12/29/2015.
 */
public class StreamService extends Service implements OnCompletionListener,
        OnPreparedListener, OnErrorListener,
        OnInfoListener, OnBufferingUpdateListener {
    private static final int NOTIFICATION_ID = 1;
    private static final String notifTitle = "Playing Radio Stream";
    private static final String stopStream = "Stop";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String streamLink;
    private String streamName;
    private NotificationManager notificationManager;
    private String country_code;
    private String name;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Runnable Timer", "stop the media after 12 seconds");
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                deleteNotification();
                showNotif();
            }
        }
    };
//    private DefaultBinder mBinder;

   @Override
    public void onCreate() {
//       mBinder = new DefaultBinder(this);

       mediaPlayer.setOnCompletionListener(this);  //what happens when mediaplayer finishes player
       mediaPlayer.setOnErrorListener(this);   //what hapens on erorr
       mediaPlayer.setOnPreparedListener(this);    //media player prepared before. finds clip and starts pulling data
       mediaPlayer.setOnBufferingUpdateListener(this); //if clip needs to buffer, this can send mesafges
       mediaPlayer.setOnInfoListener(this);    //listening for info if need info sent out from lsitneer
//       resetMedia();
       mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        resetMedia();
        mediaPlayer.reset();

        // Set up the MediaPlayer data source using the strAudioLink value
        if (!mediaPlayer.isPlaying()) {
            try {
                streamLink = intent.getExtras().getString("streamLink");
                streamName = intent.getExtras().getString("streamName");
                country_code = intent.getExtras().getString("country_code");
                if (streamLink != null) {
//                    streamLink = "http://streaming.radionomy.com/70sClassics?lang=en-US%2cen%3bq%3d0.8%2czh-TW%3bq%3d0.6%2czh%3bq%3d0.4";
//                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
                    mediaPlayer.setDataSource(streamLink);
                    Log.d("showing link", streamLink);
                    Log.d("showing mediaPlayer", mediaPlayer.toString());
                    mediaPlayer.prepareAsync(); //async
                    createNotification();
                    handler = new Handler();
                    Log.d("State 1", "Before on stop");
                    onStop();
                    Log.d("State 2", "After on stop");
                    onStart();
                    Log.d("State 3", "After on onstart");


                }
                else {
                    deleteNotification();
                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this,
                        "Argument Error for stream", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalStateException e) {
                Toast.makeText(this,
                        "Media Player state incorrect", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this,
                        "Data Source failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(this,
                        "Media player error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        //dont end unless we tell it to
        return START_NOT_STICKY;
    }

    private void releaseMedia() {
        mediaPlayer.release();
        onStop();
    }
    private void resetMedia() {
        mediaPlayer.reset();
        onStop();
    }
    private void showNotif() {
        Toast.makeText(this,
                "Media Player Timeout", Toast.LENGTH_LONG).show();
    }
    protected void onStart() {
        handler.postDelayed(runnable, 45 * 1000);
    }

    protected void onStop() {
        try {
            handler.removeCallbacks(runnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        Log.d("onDestroy", "");
        super.onDestroy();
        deleteNotification();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {

                mediaPlayer.stop();
            }
            //releases memory from MediaPlayer
//            mediaPlayer.reset();
//            mediaPlayer.release();
            resetMedia();
            releaseMedia();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
        Log.d("onBufferingUpdate", Integer.toString(arg1));
        Toast.makeText(this,
                "Buffering...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        Log.d("onInfo", "in here");
        return false;
    }

    //---Replace ---
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        deleteNotification();
        Log.d("onError", "An error has occurred with media player!!!");

        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this,
                        "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(this, "MEDIA ERROR TIMED OUT " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Toast.makeText(this, "MEDIA ERROR UNSUPPORTED " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(this, "MEDIA ERROR IO " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(this, "MEDIA ERROR MALFORMED " + extra,Toast.LENGTH_SHORT).show();

            default:
                Toast.makeText(this, "CONNECTION ERROR " + extra,
                        Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {
        Log.e("I", arg0.toString());
        if (arg0.equals(mediaPlayer)){
        Log.e("I", "Media player has been loaded to memory !");
        }
        //play stream once mediaplayer is ready
        Log.d("onPrepared", "play media!!");
        playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // When song ends, need to tell activity to display "Play" button
        stopMedia();
        stopSelf(); //stop service
    }

    //---Replace ---
    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    //---Replace ---
    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void createNotification() {
        NotificationCompat.Builder mBuilder;

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        Intent notifIntent = new Intent(getBaseContext(), StreamService.class);
        notifIntent.putExtra("country_code", country_code);
        notifIntent.putExtra("name", streamName);
        notifIntent.putExtra("stop", stopStream);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), notifIntent, 0);
        mBuilder = new NotificationCompat.Builder(this)
                .addAction(R.mipmap.stop, "Stop", pIntent)
                .setSmallIcon(R.mipmap.headset)
                .setContentTitle(notifTitle)
                .setContentIntent(pendingIntent)
                .setContentText(streamName)
                .setOngoing(true);

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        final Notification notification = mBuilder.build();
//        startForeground(NOTIFICATION_ID, notification);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    private void deleteNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void stopPlayingStream() {

        deleteNotification();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {

                mediaPlayer.stop();
            }
            //releases memory from MediaPlayer
            resetMedia();
            releaseMedia();
//            mediaPlayer.reset();
//            mediaPlayer.release();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
//        return mBinder;
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        deleteNotification();
    }
}
