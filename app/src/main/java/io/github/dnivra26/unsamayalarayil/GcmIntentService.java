package io.github.dnivra26.unsamayalarayil;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

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
            Log.d(TAG, "Send error: " + extras.toString());
        } else if (GoogleCloudMessaging.
                MESSAGE_TYPE_DELETED.equals(messageType)) {
            Log.d(TAG, "Deleted messages on server: " +
                    extras.toString());
        } else if (GoogleCloudMessaging.
                MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            JsonParser jsonParser = new JsonParser();
            String jsonMessage = extras.getString("message");
            String type = extras.getString("type");
            JsonObject jsonObject = jsonParser.parse(jsonMessage).getAsJsonObject();
            if (type.equalsIgnoreCase("new_device")) {
                String deviceId = jsonObject.get("device_id").getAsString();
                sendNewDeviceNotification(getResources().getString(R.string.new_device_alert), deviceId);
            } else if (type.equalsIgnoreCase("alert")) {
                String deviceId = jsonObject.get("device_id").getAsString();
                String item = jsonObject.get("item_name").getAsString();
                String currentPercentage = jsonObject.get("current_percentage").getAsString();
                sendAlertNotification(item, currentPercentage);
                try {
                    DB snappydb = DBFactory.open(this);
                    ItemAction object = snappydb.getObject(item, ItemAction.class);
                    if (object.getAction().equals("SMS")) {
                        sendMessage(item, object.getPhoneNumber());
                    } else if (object.getAction().equals("location")) {
                        addLocationReminder(item, Double.parseDouble(object.getLattitude()), Double.parseDouble(object.getLongitude()));
                    }
                } catch (SnappydbException e) {
                    e.printStackTrace();
                }
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void addLocationReminder(String itemnName, Double lattitude, Double longitude) {
        GeoFencingService geoFencingService = new GeoFencingService(this);
        geoFencingService.addLocationReminder(itemnName, lattitude, longitude);
    }

    private void sendMessage(String item, String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "Please send some amount of " + item, null, null);
        Log.i(TAG, "Message sent");
    }

    private void sendAlertNotification(String item, String currentPercentage) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        String msg = item.toUpperCase() + " is below " + currentPercentage + "%. Buy some.";
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_add_shopping_cart_white_24dp)
                        .setContentTitle(getResources().getString(R.string.notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNewDeviceNotification(String msg, String deviceName) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, NewDeviceActivity.class);
        intent.putExtra(device_name, deviceName);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_add_shopping_cart_white_24dp)
                        .setContentTitle(getResources().getString(R.string.notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true);


        mBuilder.setContentIntent(contentIntent);
        Notification build = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, build);
    }


}
