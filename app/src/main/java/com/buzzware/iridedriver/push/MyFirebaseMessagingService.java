package com.buzzware.iridedriver.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.buzzware.iridedriver.Fragments.HomeFragment;
import com.buzzware.iridedriver.R;
import com.buzzware.iridedriver.Screens.Home;
import com.buzzware.iridedriver.Screens.MessagesActivity;
import com.buzzware.iridedriver.Screens.StartUp;
import com.buzzware.iridedriver.events.NewRideEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;


/**
 * Created by prabh on 25-10-2017.
 */

public class
MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";

    private Bitmap bitmap;

    private Context context = this;

    public static final String FIREBASE_TOKEN = "token";

    @Override
    public void onNewToken(@NonNull String refreshedToken) {
        super.onNewToken(refreshedToken);
//        SharedPrefUtil.getInstance(this).put(FIREBASE_TOKEN, refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> map = remoteMessage.getData();

        String status = null;

        String id = null;
        try {

            if (map.keySet().toArray().length > 1) {

                status = map.get(map.keySet().toArray()[0].toString());

                id = map.get(map.keySet().toArray()[1].toString());

                if (status.equalsIgnoreCase("booked")) {

                    EventBus.getDefault().post(new NewRideEvent(id));

                }

            } else if (map.keySet().toArray().length == 1) {

                id = map.get(map.keySet().toArray()[0].toString());

            }

        } catch (Exception e) {


        }

        if (remoteMessage.getNotification().getTitle() != null && remoteMessage.getNotification().getBody() != null) {


            if (remoteMessage.getData().size() > 0) {

                String title = remoteMessage.getData().get("title");
                String text = remoteMessage.getData().get("text");

                if(title == null)
                    title = remoteMessage.getNotification().getTitle();
                if(text == null)
                    text = remoteMessage.getNotification().getBody();

                sendUserNotification(title, text,status,id);
            }
        }

    }


    private void sendUserNotification(String title, String mess, String status, String id) {

        int notifyID = 1;

        Intent intent = null;

        NotificationChannel mChannel;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (title.toLowerCase().contains("promo")) {

            intent = new Intent(context, Home.class);

            intent.putExtra("action", "promotionFragment");
            intent.putExtra("newRemainder", true);
        } else if (title.equalsIgnoreCase("New Reminder")) {

            intent = new Intent(context, Home.class);

            intent.putExtra("action", "remainderFragment");
            intent.putExtra("newRemainder", true);

        } else if (status != null) {

            intent = new Intent(context, Home.class);
            intent.putExtra("action", "homeFragment");
            intent.putExtra("newRide", false);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else {

            if (title.toLowerCase().contains("remainder")) {

                intent = new Intent(context, Home.class);

                intent.putExtra("action", "homeFragment");
                intent.putExtra("newRemainder", true);

            } else if (id != null) {
                intent = new Intent(context, MessagesActivity.class);
                intent.putExtra("conversationID", id);
                intent.putExtra("checkFrom", "false");
            } else if (status.equalsIgnoreCase("booked")) {

                intent.putExtra("action", "homeFragment");
                intent.putExtra("newRide", true);

            } else {

                intent = new Intent(context, StartUp.class);
            }

        }


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName();// The id of the channel.
        CharSequence name = "Sample one";// The user-visible name of the channel.
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(mess));
        notificationBuilder.setContentText(mess);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }


    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        return R.mipmap.ic_launcher;

    }

}