package io.github.dnivra26.unsamayalarayil;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = GcmIntentService.class.getSimpleName();
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String device_name = "device_name";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public GcmIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.
                MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            Log.d(TAG,"Send error: " + extras.toString());
        } else if (GoogleCloudMessaging.
                MESSAGE_TYPE_DELETED.equals(messageType)) {
            Log.d(TAG,"Deleted messages on server: " +
                    extras.toString());
        } else if (GoogleCloudMessaging.
                MESSAGE_TYPE_MESSAGE.equals(messageType)) {

            sendNotification(getResources().getString(R.string.new_device_alert), "device_name");
            Log.i(TAG, "Received: " + extras.toString());

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, String deviceName) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NewDeviceActivity.class);
        intent.putExtra(device_name,deviceName);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_drawer)
                        .setContentTitle(getResources().getString(R.string.notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}
