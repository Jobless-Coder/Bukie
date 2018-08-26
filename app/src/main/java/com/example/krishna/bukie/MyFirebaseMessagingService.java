/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.krishna.bukie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.bumptech.glide.Glide;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "hellonoob";
    private int notif=0;
    private MyChatsStatus myChatsStatus;
    private String identity;
    private Map<String,Integer> idMap=new HashMap<>();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("hello",remoteMessage.getData().toString()+"kkk");



        if (remoteMessage.getData().size() > 0) {
            try {
                sendNotification(remoteMessage);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if ( true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            try {
                sendNotification(remoteMessage);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    private void scheduleJob() {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);

    }


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(RemoteMessage messageBody) throws ExecutionException, InterruptedException {
        Log.d("hello","kkkkk");
        Map<String,String> hashMap=new HashMap<>();
        hashMap=messageBody.getData();
        if(!idMap.containsKey(hashMap.get("chatid"))) {
            notif++;
            idMap.put(hashMap.get("chatid"),notif);
        }
        String icon,fullname;
        Log.i("notifs",hashMap.toString());
        myChatsStatus=new MyChatsStatus(hashMap.get("sellerid"),hashMap.get("buyerid"),hashMap.get("adid"),hashMap.get("coverpic"),hashMap.get("chatid"),hashMap.get("sellerpic"),hashMap.get("buyerpic"),hashMap.get("sellerfullname"),hashMap.get("buyerfullname"));
        identity=hashMap.get("identity");
        if(identity.equals("seller")) {
            icon=hashMap.get("sellerpic");
            identity = "buyer";
            fullname=hashMap.get("sellerfullname");
        }
        else {
            icon=hashMap.get("buyerpic");
            identity = "seller";
            fullname=hashMap.get("buyerfullname");
        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("mychats",myChatsStatus);
        intent.putExtra("identity",identity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        //Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap bitmap;
        if(icon!=null)
         bitmap= Glide.with(getApplication()).load(icon).asBitmap().into(-1,-1).get();
        else
             bitmap= Glide.with(getApplication()).load(R.drawable.bookpic).asBitmap().into(-1,-1).get();
        NotificationCompat.BigTextStyle bigTextStyle= new NotificationCompat.BigTextStyle().bigText(hashMap.get("message")).setBigContentTitle(fullname);



        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.profile)
                        .setLargeIcon(bitmap)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setStyle(bigTextStyle)
                       .setGroupSummary(true)
                        .setGroup("com.example.krishna.bukie")
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        int position=idMap.get(hashMap.get("chatid"));
        manager.notify(position /* ID of notification */, notificationBuilder.build());
    }


    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);


    }
}
