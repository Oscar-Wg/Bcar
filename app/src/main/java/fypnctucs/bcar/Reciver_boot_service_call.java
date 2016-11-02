package fypnctucs.bcar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by kamfu.wong on 30/10/2016.
 */

public class Reciver_boot_service_call extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // service
            intent = new Intent(context, Service_ble_connection.class);
            context.startService(intent);
            Log.d("DEBUG", "service started");
        }
    }
}
