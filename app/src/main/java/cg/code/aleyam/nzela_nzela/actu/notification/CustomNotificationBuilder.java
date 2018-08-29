package cg.code.aleyam.nzela_nzela.actu.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cg.code.aleyam.nzela_nzela.R;


import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class CustomNotificationBuilder {

    boolean isBound = false;
    ServiceConnection serviceConnection = null;
    AlertsRequestsService alertsRequestsService = null;

    private static CustomNotificationBuilder cnb = null;

    public static CustomNotificationBuilder getInstance() {
        if(cnb == null) {
            cnb = new CustomNotificationBuilder();
        }
        return cnb;
    }

    private CustomNotificationBuilder() {

    }

    public void createNotification(Context context
            , String bigTextText
            , String summaryText
            , String contentTitle
            , String contentText
            , Class IntentClass
            , Bundle bundle) {
        //on definit la sonnerie de la notification.
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context , "notify_001");
        Intent notificationIntent = new Intent(context , IntentClass);
        //on dit bien a l'activite que ca vient d'ici.
        if(bundle != null) {
            notificationIntent.putExtras(bundle);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        //information quand la notification est depliee.
        bigText.setBigContentTitle("ARIV");
        bigText.bigText(bigTextText);
        bigText.setSummaryText(summaryText);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        //deux seconde de vibration
        mBuilder.setVibrate(new long[]{2000});
        mBuilder.setSmallIcon(R.drawable.app_icon);
        //titre de la notification quand elle est pliee.
        mBuilder.setContentTitle(contentTitle);
        //apercu
        mBuilder.setContentText(contentText);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        //sur android oreo les notification sont categorisee en chaine donc ce snipet s'impose.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    public boolean bindRequestService(final Context context , final Intent serviceIntent) {
        //TODO le boolean la n'est pas logiquement generique
        if(!isBound && serviceConnection == null) {
            isBound = true;
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    alertsRequestsService = ((AlertsRequestsService.RequestBinder) iBinder).getHostService();
                    alertsRequestsService.setServiceConnection(serviceConnection);
                    Log.e("test", "onServiceConnected: connexion...");
                    isBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
//                    if(isBound) {
//                        context.unbindService(serviceConnection);
//                    }
                    isBound = false;
                    serviceConnection = null;
                    context.stopService(serviceIntent);
                }
            };
            context.bindService(serviceIntent , serviceConnection , Context.BIND_AUTO_CREATE );
        }

        Log.e("test", "onServiceConnected: serviceConnected");
        return isBound;
    }

}
