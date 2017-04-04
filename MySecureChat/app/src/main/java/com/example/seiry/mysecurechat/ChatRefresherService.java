package com.example.seiry.mysecurechat;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ChatRefresherService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_REFRESH = "com.example.seiry.mysecurechat.action.REFRESH";
    private static final String TAG = "ChatRefresherService";
    public ChatRefresherService() {
        super("ChatRefresherService");
    }
    private Context context;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRefresh(Context context) {
        Intent intent = new Intent(context, ChatRefresherService.class);
        intent.setAction(ACTION_REFRESH);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                handleActionRefresh();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRefresh() {
        Log.v(TAG, "Starting refresh");
        // Loong work
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Finished refresh");
    }


}
