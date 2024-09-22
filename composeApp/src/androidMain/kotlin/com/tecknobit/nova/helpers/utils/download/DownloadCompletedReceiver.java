package com.tecknobit.nova.helpers.utils.download;

import static android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE;
import static android.app.DownloadManager.ACTION_VIEW_DOWNLOADS;
import static android.app.DownloadManager.EXTRA_DOWNLOAD_ID;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.tecknobit.nova.ui.activities.navigation.Splashscreen.assetDownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The {@code DownloadCompletedReceiver} class is useful to receive the
 * {@link DownloadManager#ACTION_DOWNLOAD_COMPLETE} action and then open the file manager of the device
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see BroadcastReceiver
 */
public class DownloadCompletedReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null && action.equals(ACTION_DOWNLOAD_COMPLETE)) {
            if(intent.getLongExtra(EXTRA_DOWNLOAD_ID, -1) == assetDownloader.getLastDownloadToWait()) {
                Intent fileManager = new Intent(ACTION_VIEW_DOWNLOADS);
                fileManager.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(fileManager);
            }
        }
    }

}
