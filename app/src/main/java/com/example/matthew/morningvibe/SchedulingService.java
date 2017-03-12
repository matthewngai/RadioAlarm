package com.example.matthew.morningvibe;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Matthew on 12/31/2015.
 */
public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
    }

    Intent serviceIntent;
    public static final String TAG = "Scheduling Demo";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds
    // the string, it indicates the presence of a doodle.
    public static final String SEARCH_STRING = "doodle";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    public static final String URL = "http://www.google.com";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private String streamLink;
    private String streamCountry;

    @Override
    protected void onHandleIntent(Intent intent) {
        streamCountry = intent.getExtras().getString("streamCountry");
        streamLink = intent.getExtras().getString("streamLink");
        System.out.println("on handle intent serivce");
        System.out.println(streamCountry);
        System.out.println(streamLink);
        // BEGIN_INCLUDE(service_onhandle)
        // The URL from which to fetch content.
        String urlString = URL;

        String result ="";

        // Try to connect to the Google homepage and download content.
//        try {
//            result = loadFromNetwork(urlString);
//        } catch (IOException e) {
//            Log.i(TAG, getString(R.string.connection_error));
//        }

        // If the app finds the string "doodle" in the Google home page content, it
        // indicates the presence of a doodle. Post a "Doodle Alert" notification.
//        if (result.indexOf(SEARCH_STRING) != -1) {
//            sendNotification(getString(R.string.doodle_found));
//            Log.i(TAG, "Found doodle!!");
//        } else {
//            sendNotification(getString(R.string.no_doodle));
//            Log.i(TAG, "No doodle found. :-(");
//        }
        // Release the wake lock provided by the BroadcastReceiver.
        System.out.println("WAKE UP!!!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~");
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Vibrator vibrator = (Vibrator) this.getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(3000);
//        Ringtone ringtone = RingtoneManager.getRingtone(this.getBaseContext(), uri);
//        ringtone.play();
        serviceIntent = new Intent(this, StreamService.class);
        serviceIntent.putExtra("streamLink", streamLink);
        startService(serviceIntent);
        AlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }

    // Post a notification indicating whether a doodle was found.
//    private void sendNotification(String msg) {
//        mNotificationManager = (NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, MainActivity.class), 0);
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(getString(R.string.doodle_alert))
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
//                        .setContentText(msg);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }

//

    public void cancelAlarm() {
        try {
            System.out.println("Stop alarm stream");
            stopService(serviceIntent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
