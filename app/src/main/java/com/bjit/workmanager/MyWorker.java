package com.bjit.workmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;

import androidx.work.Data;
import androidx.work.Worker;

public class MyWorker extends Worker {

    public static final String EXTRA_TITLE = "Title of notification";
    public static final String EXTRA_TEXT = "Details of notification";
    public static final String EXTRA_OUTPUT_MESSAGE = "output_message";

    private int NOTIFICATION_ID = 123;
    private String CHANNEL_ID = "channel_id";
    private String CHANNEL_NAME = "channel_name";

    @NonNull
    @Override
    public Result doWork() {

        // Observe trigger time
        long currentTime = System.currentTimeMillis();
        String strCurrentTime = new SimpleDateFormat("dd HH:mm aa").format(currentTime);
        Log.d("Test_Work_Manager", strCurrentTime);

        sendNotification("Execute Work Manager", "I have been send by WorkManager!");

        Data output = new Data.Builder()
                .putString(EXTRA_OUTPUT_MESSAGE, "I have come from MyWorker!")
                .build();

        setOutputData(output);

        return Result.SUCCESS;
    }

    public void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }
}

