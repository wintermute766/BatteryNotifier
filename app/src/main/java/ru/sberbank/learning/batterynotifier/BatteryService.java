package ru.sberbank.learning.batterynotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

public class BatteryService extends Service {

    private static final int CRITICAL_BATTERY_LEVEL = 15;

    private boolean showNotifications = true;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int capacity = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            float fPercent = ((float) level / (float) capacity) * 100f;
            int percent = Math.round(fPercent);

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (percent < CRITICAL_BATTERY_LEVEL) {
                if (showNotifications) {
                    Notification.Builder builder = new Notification.Builder(BatteryService.this);
                    builder.setSmallIcon(R.drawable.ic_stat_battery_low);
                    builder.setContentTitle(getString(R.string.warning_battery_low));

                    Intent startBattery = new Intent();
                    startBattery.setComponent(
                            new ComponentName("ru.sberbank.learning.battery", "ru.sberbank.learning.battery.MainActivity"));
                    startBattery.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    builder.setContentIntent(PendingIntent
                            .getActivity(BatteryService.this, 0, startBattery, PendingIntent.FLAG_CANCEL_CURRENT));


                    Intent startSelf = new Intent(BatteryService.this, BatteryService.class);
                    startSelf.setAction(Intent.ACTION_DELETE);
                    builder.setDeleteIntent(PendingIntent
                            .getService(BatteryService.this, 0, startSelf, PendingIntent.FLAG_CANCEL_CURRENT));

                    Notification notification = builder.getNotification();

                    nm.notify(R.string.warning_battery_low, notification);
                }
            } else {
                nm.cancel(R.string.warning_battery_low);
                showNotifications = true;
            }
        }
    };

    public BatteryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && Intent.ACTION_DELETE.equals(intent.getAction())) {
            showNotifications = false;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancelAll();
    }
}
