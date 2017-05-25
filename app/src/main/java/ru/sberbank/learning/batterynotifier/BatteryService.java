package ru.sberbank.learning.batterynotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

public class BatteryService extends Service {

    private static final int CRITICAL_BATTERY_LEVEL = 15;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int capacity = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            float fPercent = ((float) level / (float) capacity) * 100f;
            int percent = Math.round(fPercent);

            if (percent < CRITICAL_BATTERY_LEVEL) {
                Notification.Builder builder = new Notification.Builder(BatteryService.this);
                builder.setSmallIcon(R.drawable.ic_stat_battery_low);
                builder.setContentTitle(getString(R.string.warning_battery_low));
                Notification notification = builder.getNotification();

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(R.string.warning_battery_low, notification);
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
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }
}
