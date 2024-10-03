package com.tecknobit.nova.helpers.utils

import com.mmk.kmpnotifier.notification.NotifierManager
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.Requester
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.nova.DESTINATION_KEY
import com.tecknobit.nova.cache.LocalSessionHelper
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.novacore.records.NovaNotification
import com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_IDENTIFIER_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_IDENTIFIER_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_KEY
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.new_assets_are_ready_to_be_tested
import nova.composeapp.generated.resources.new_release_has_been_created
import nova.composeapp.generated.resources.the_project_has_been_deleted
import nova.composeapp.generated.resources.the_release_has_been_approved
import nova.composeapp.generated.resources.the_release_has_been_deleted
import nova.composeapp.generated.resources.the_release_has_been_promoted_to_alpha
import nova.composeapp.generated.resources.the_release_has_been_promoted_to_beta
import nova.composeapp.generated.resources.the_release_has_been_promoted_to_latest
import nova.composeapp.generated.resources.the_release_has_been_rejected
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.json.JSONException

/**
 * The [NotificationChecker] class is useful to check any notifications available to send to the user
 * while the desktop application is hidden
 *
 * @author N7ghtm4r3 - Tecknobit
 */
class NotificationChecker {

    /**
     * **notifier** the notifier used to send the local notifications
     */
    private val notifier = NotifierManager.getLocalNotifier()

    /**
     * Method to exec the routine to check if there are any notifications to send <br></br>
     *
     * No-any params required
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun execCheckRoutine() {
        GlobalScope.launch {
            while (!isNotificationsFetchingEnable()) {
                val localSessionHelper = LocalSessionHelper(DatabaseDriverFactory())
                for (session in localSessionHelper.getSessions()) {
                    if (session.isHostSet) {
                        val requester = NovaRequester(
                            session.hostAddress,
                            session.id,
                            session.token,
                            false
                        )
                        val response = JsonHelper(requester.getNotifications())
                        try {
                            if (response.getString(Requester.RESPONSE_STATUS_KEY) == SUCCESSFUL.name) {
                                val jNotifications = response.getJSONArray(RESPONSE_MESSAGE_KEY)
                                for (j in 0 until jNotifications.length()) {
                                    val notification =
                                        NovaNotification(jNotifications.getJSONObject(j))
                                    if (!notification.isSent) {
                                        sendNotification(
                                            notification = notification
                                        )
                                    }
                                }
                            }
                        } catch (ignored: JSONException) {
                        }
                    }
                }
                delay(10000L)
            }
        }
    }

    /**
     * Method to create and send a notification
     *
     * @param notification: the notification details to send and create the related notification
     */
    private fun sendNotification(
        notification: NovaNotification
    ) {
        val releaseVersion = notification.releaseVersion
        val contentText = getContentText(
            releaseId = notification.releaseId,
            releaseStatus = notification.status
        )!!
        MainScope().launch {
            val content = getString(
                resource = contentText
            )
            val payload = getPayload(
                notification = notification
            )
            if (releaseVersion != null) {
                notifier.notify(
                    title = releaseVersion,
                    body = content,
                    payloadData = payload
                )
            } else {
                notifier.notify(
                    title = content,
                    body = "",
                    payloadData = payload
                )
            }
        }
    }

    /**
     * Function to get the correct text to use in the notification UI message
     *
     * @param releaseId: the release identifier
     * @param releaseStatus: the release status
     *
     * @return the correct text to use as int
     */
    private fun getContentText(
        releaseId: String?,
        releaseStatus: ReleaseStatus?
    ): StringResource? {
        return if (releaseStatus == null) {
            if (releaseId == null) Res.string.the_project_has_been_deleted
            else Res.string.the_release_has_been_deleted
        } else {
            when (releaseStatus) {
                ReleaseStatus.New -> Res.string.new_release_has_been_created
                ReleaseStatus.Verifying -> Res.string.new_assets_are_ready_to_be_tested
                ReleaseStatus.Rejected -> Res.string.the_release_has_been_rejected
                ReleaseStatus.Approved -> Res.string.the_release_has_been_approved
                ReleaseStatus.Alpha -> Res.string.the_release_has_been_promoted_to_alpha
                ReleaseStatus.Beta -> Res.string.the_release_has_been_promoted_to_beta
                ReleaseStatus.Latest -> Res.string.the_release_has_been_promoted_to_latest
                else -> null
            }
        }
    }

    /**
     * Function to get the payload data about the [notification] parameter
     *
     * @param notification: the notification from build a payload
     *
     * @return the payload as [Map] of [String] of [String]
     */
    private fun getPayload(
        notification: NovaNotification
    ): Map<String, String> {
        val payload = mutableMapOf<String, String>()
        payload[IDENTIFIER_KEY] = notification.user.id
        val releaseId = notification.releaseId
        if (releaseId == null)
            payload[DESTINATION_KEY] = PROJECTS_KEY
        else {
            payload[PROJECT_IDENTIFIER_KEY] = notification.projectId
            if (notification.status != null) {
                payload[DESTINATION_KEY] = RELEASE_KEY
                payload[RELEASE_IDENTIFIER_KEY] = releaseId
            } else
                payload[DESTINATION_KEY] = PROJECT_KEY
        }
        return payload
    }

}