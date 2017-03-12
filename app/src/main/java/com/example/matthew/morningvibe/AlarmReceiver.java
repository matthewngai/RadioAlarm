package com.example.matthew.morningvibe;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Matthew on 12/31/2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    private String streamLink;
    private String streamCountry;

    @Override
    public void onReceive(final Context context, Intent intent) {
        streamCountry = intent.getExtras().getString("streamCountry");
        streamLink = intent.getExtras().getString("streamLink");

        Intent service = new Intent(context, SchedulingService.class);
        // Start the service, keeping the device awake while it is launching.
        System.out.println(streamCountry);
        System.out.println(streamLink);
        service.putExtra("streamCountry", streamCountry);
        service.putExtra("streamLink", streamLink);
        startWakefulService(context, service);

//START HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//        System.out.println("WAKE UP!!!!!!!!!!!!!!!!!!!!~~~~~~~~~~~~~");
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate(2000);
//        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
//        ringtone.play();
    }
}
