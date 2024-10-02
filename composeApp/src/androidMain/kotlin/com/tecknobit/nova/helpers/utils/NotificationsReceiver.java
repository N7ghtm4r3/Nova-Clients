package com.tecknobit.nova.helpers.utils;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.media.RingtoneManager.getDefaultUri;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.equinox.Requester.RESPONSE_MESSAGE_KEY;
import static com.tecknobit.equinox.Requester.RESPONSE_STATUS_KEY;
import static com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.nova.AppKt.DESTINATION_KEY;
import static com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY;
import static com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.project.Project.PROJECT_KEY;
import static com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.release.Release.RELEASE_KEY;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.nova.MainActivity;
import com.tecknobit.nova.R;
import com.tecknobit.nova.cache.LocalSessionHelper;
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory;
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession;
import com.tecknobit.novacore.records.NovaNotification;
import com.tecknobit.novacore.records.release.Release.ReleaseStatus;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The {@code NotificationsReceiver} class is useful to receive the
 * {@link Intent#ACTION_BOOT_COMPLETED} action and then make the routine to check if there are
 * any notifications to send to the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see BroadcastReceiver
 */
public class NotificationsReceiver extends BroadcastReceiver {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            NotificationsHelper notificationsHelper = new NotificationsHelper(context);
            notificationsHelper.scheduleAndExec();
        }
    }

    /**
     * The {@code NotificationsHelper} class is useful to send the notifications to the user
     *
     * @author N7ghtm4r3 - Tecknobit
     */
    public static class NotificationsHelper {

        /**
         * {@code WORKER_NAME} the name assigned to the {@link #periodicRequest}
         */
        private static final String WORKER_NAME = "notificationsChecker";

        /**
         * {@code NOVA_NOTIFICATIONS_CHANNEL_ID} the identifier of the notifications channel
         */
        private static final String NOVA_NOTIFICATIONS_CHANNEL_ID = NOTIFICATIONS_KEY;

        /**
         * {@code periodicRequest} the periodic request to execute to check if there are any notification
         * to send, the check it is schedule every 15 minutes
         */
        private static final PeriodicWorkRequest periodicRequest = new PeriodicWorkRequest.Builder(
                NotificationsWorker.class,
                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS
        ).setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build())
                .addTag(WORKER_NAME)
                .build();

        /**
         * {@code notificationManager} the manager which manage and send the notifications
         */
        private final NotificationManager notificationManager;

        /**
         * {@code context} the context where the {@link NotificationsHelper} has been invoked
         */
        private final Context context;

        /**
         * {@code workManager} the manager who execute and schedule the {@link #periodicRequest}
         */
        private final WorkManager workManager;

        /**
         * Constructor to init the {@link NotificationsHelper} class
         *
         * @param context: the context where the {@link NotificationsHelper} has been invoked
         */
        public NotificationsHelper(Context context) {
            this.context = context;
            workManager = WorkManager.getInstance(context);
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            createNotificationsChannel();
        }

        /**
         * Method to create the notifications channel <br>
         *
         * No-any params required
         */
        private void createNotificationsChannel() {
            if(notificationManager.getNotificationChannel(NOVA_NOTIFICATIONS_CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                        NOVA_NOTIFICATIONS_CHANNEL_ID,
                        context.getString(R.string.release_events),
                        NotificationManager.IMPORTANCE_HIGH
                );
                AudioAttributes.Builder audioAttributes = new AudioAttributes.Builder();
                audioAttributes.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
                audioAttributes.setUsage(AudioAttributes.USAGE_NOTIFICATION);
                channel.setSound(getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes.build());
                channel.setDescription(context.getString(R.string.notifications_channel_description));
                notificationManager.createNotificationChannel(channel);
            }
        }

        /**
         * Method to schedule and exec the notifications check. <br>
         * The {@link #scheduleRoutine()} and the {@link #execCheckRoutine()} methods will be invoked <br>
         *
         * No-any params required
         */
        public void scheduleAndExec() {
            scheduleRoutine();
            execCheckRoutine();
        }

        /**
         * Method to schedule the routine using the {@link #workManager} <br>
         *
         * No-any params required
         */
        public void scheduleRoutine() {
            workManager.enqueueUniquePeriodicWork(
                    WORKER_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicRequest
            );
        }

        /**
         * Method to exec the routine to check if there are any notifications to send <br>
         *
         * No-any params required
         */
        public void execCheckRoutine() {
            try(LocalSessionHelper localSessionHelper = new LocalSessionHelper(new DatabaseDriverFactory())) {
                for (NovaSession session : localSessionHelper.getSessions()) {
                    if(session.isHostSet()) {
                        NovaRequester requester = new NovaRequester(
                                session.getHostAddress(),
                                session.getId(),
                                session.getToken(),
                                false
                        );
                        JsonHelper response = new JsonHelper(requester.getNotifications());
                        try {
                            if(response.getString(RESPONSE_STATUS_KEY).equals(SUCCESSFUL.name())) {
                                JSONArray jNotifications = response.getJSONArray(RESPONSE_MESSAGE_KEY);
                                for (int j = 0; j < jNotifications.length(); j++) {
                                    NovaNotification notification = new NovaNotification(jNotifications
                                            .getJSONObject(j));
                                    if(!notification.isSent())
                                        sendNotification(session, notification, getDestination(notification));
                                }
                            }
                        } catch (JSONException ignored) {}
                    }
                }
            }
        }

        /**
         * Method to get the destination to reach after the user clicked on a specific {@link Notification}
         *
         * @param notification: the notification from get the correct details to get the destination
         * @return the destination to reach as {@link Intent}
         */
        private Intent getDestination(NovaNotification notification) {
            Intent destination = new Intent(context, MainActivity.class);
            String releaseId = notification.getReleaseId();
            if(releaseId == null)
                destination.putExtra(DESTINATION_KEY, PROJECTS_KEY);
            else {
                destination.putExtra(PROJECT_IDENTIFIER_KEY, notification.getProjectId());
                if(notification.getStatus() != null) {
                    destination.putExtra(DESTINATION_KEY, RELEASE_KEY);
                    destination.putExtra(RELEASE_IDENTIFIER_KEY, releaseId);
                } else
                    destination.putExtra(DESTINATION_KEY, PROJECT_KEY);
            }
            destination.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            destination.putExtra(IDENTIFIER_KEY, notification.getUser().getId());
            return destination;
        }

        /**
         * Method to create and send a notification
         *
         * @param session: the session where the notifications are attached
         * @param notification: the notification details to send and create the related {@link Notification}
         * @param destination: the destination to reach after user clicked on the {@link Notification}
         */
        private void sendNotification(NovaSession session, NovaNotification notification, Intent destination) {
            Notification.Builder builder = new Notification.Builder(context, NOVA_NOTIFICATIONS_CHANNEL_ID);
            builder.setSmallIcon(R.drawable.logo);
            String releaseVersion = notification.getReleaseVersion();
            String text = context.getString(getContentText(notification.getReleaseId(),
                    notification.getStatus()));
            if(releaseVersion != null) {
                builder.setContentTitle(releaseVersion);
                builder.setContentText(text);
            } else
                builder.setContentTitle(text);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(
                        session.getHostAddress() + "/" + notification.getProjectLogo()
                ).openConnection().getInputStream());
                builder.setLargeIcon(bitmap);
            } catch (IOException ignored) {
            }
            builder.setContentIntent(getActivity(context, 0, destination, FLAG_UPDATE_CURRENT));
            builder.setAutoCancel(true);
            notificationManager.notify(new Random().nextInt(), builder.build());
        }

        /**
         * Function to get the correct text to use in the notification UI message
         *
         * @param releaseId: the release identifier
         * @param releaseStatus: the release status
         *
         * @return the correct text to use as int
         */
        private int getContentText(String releaseId, ReleaseStatus releaseStatus) {
            if(releaseStatus == null) {
                if(releaseId == null)
                    return R.string.the_project_has_been_deleted;
                else
                    return R.string.the_release_has_been_deleted;
            } else {
                return switch (releaseStatus) {
                    case New -> R.string.new_release_has_been_created;
                    case Verifying -> R.string.new_assets_are_ready_to_be_tested;
                    case Rejected -> R.string.the_release_has_been_rejected;
                    case Approved -> R.string.the_release_has_been_approved;
                    case Alpha -> R.string.the_release_has_been_promoted_to_alpha;
                    case Beta -> R.string.the_release_has_been_promoted_to_beta;
                    case Latest -> R.string.the_release_has_been_promoted_to_latest;
                    default -> -1;
                };
            }
        }

    }

    /**
     * The {@code NotificationsWorker} class is useful to execute the {@link NotificationsHelper#periodicRequest}
     *
     * @author N7ghtm4r3 - Tecknobit
     * @see Worker
     */
    public static class NotificationsWorker extends Worker {

        /**
         * {@code notificationsHelper} the helper used to send the notifications
         */
        private final NotificationsHelper notificationsHelper;

        /**
         * Constructor to init the {@link NotificationsWorker} class
         *
         * @param context: the context where the {@link NotificationsWorker} has been invoked
         * @param workerParams: the params for the worker
         */
        public NotificationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            notificationsHelper = new NotificationsHelper(context);
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public Result doWork() {
            try {
                notificationsHelper.execCheckRoutine();
                return Result.success();
            } catch (Throwable e) {
                return Result.failure();
            }
        }

    }

}
