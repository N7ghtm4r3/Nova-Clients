package com.tecknobit.nova.helpers.utils

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tecknobit.apimanager.annotations.RequestPath
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.DELETE
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PATCH
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PUT
import com.tecknobit.equinox.environment.helpers.EquinoxRequester
import com.tecknobit.equinox.environment.records.EquinoxItem.IDENTIFIER_KEY
import com.tecknobit.novacore.helpers.NovaEndpoints.ADD_MEMBERS_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.COMMENT_ASSET_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.CREATE_REPORT_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.EVENTS_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.JOIN_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.LEAVE_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.MARK_MEMBER_AS_TESTER_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.PROMOTE_RELEASE_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.REMOVE_MEMBER_ENDPOINT
import com.tecknobit.novacore.helpers.NovaEndpoints.UPLOAD_ASSETS_ENDPOINT
import com.tecknobit.novacore.records.NovaNotification.NOTIFICATIONS_KEY
import com.tecknobit.novacore.records.NovaUser.EMAIL_KEY
import com.tecknobit.novacore.records.NovaUser.MEMBER_IDENTIFIER_KEY
import com.tecknobit.novacore.records.NovaUser.NAME_KEY
import com.tecknobit.novacore.records.NovaUser.PASSWORD_KEY
import com.tecknobit.novacore.records.NovaUser.PROJECTS_KEY
import com.tecknobit.novacore.records.NovaUser.ROLE_KEY
import com.tecknobit.novacore.records.NovaUser.Role
import com.tecknobit.novacore.records.NovaUser.SURNAME_KEY
import com.tecknobit.novacore.records.project.JoiningQRCode.JOIN_CODE_KEY
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.project.Project.LOGO_URL_KEY
import com.tecknobit.novacore.records.project.Project.PROJECT_MEMBERS_KEY
import com.tecknobit.novacore.records.release.Release
import com.tecknobit.novacore.records.release.Release.RELEASES_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_NOTES_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_STATUS_KEY
import com.tecknobit.novacore.records.release.Release.RELEASE_VERSION_KEY
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Approved
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.Rejected
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded.ASSETS_UPLOADED_KEY
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.REASONS_KEY
import com.tecknobit.novacore.records.release.events.RejectedReleaseEvent.TAGS_KEY
import com.tecknobit.novacore.records.release.events.RejectedTag.COMMENT_KEY
import com.tecknobit.novacore.records.release.events.ReleaseEvent.ReleaseTag
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent.RELEASE_EVENT_STATUS_KEY
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class NovaRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null,
    debugMode: Boolean = false
) : EquinoxRequester(
    host = host,
    userId = userId,
    userToken = userToken,
    debugMode = debugMode,
    connectionTimeout = 2000,
    connectionErrorMessage = DEFAULT_CONNECTION_ERROR_MESSAGE,
    enableCertificatesValidation = true
) {

    /**
     * Function to execute the request to get the potential members for a [Project]
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}", method = GET)
    fun getPotentialMembers(): JSONObject {
        return execGet(
            endpoint = assembleUsersEndpointPath()
        )
    }

    /**
     * Function to execute the request to get the user notifications
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/notifications", method = GET)
    fun getNotifications(): JSONObject {
        return execGet(
            endpoint = assembleUsersEndpointPath("/$NOTIFICATIONS_KEY")
        )
    }

    /**
     * Function to execute the request to list the projects of the user
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects", method = GET)
    fun listProjects() : JSONObject {
        return execGet(
            endpoint = assembleProjectsEndpointPath()
        )
    }

    /**
     * Function to execute the request to add a new project
     *
     * @param logoPic: the project logo
     * @param projectTitle: the name of the project
     * @param members: the members of the project
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects", method = POST)
    fun addProject(
        logoPic: String,
        projectTitle: String,
        members: List<String>
    ) : JSONObject {
        val body = createProjectPayload(
            logoPic = File(logoPic),
            projectTitle = projectTitle,
            members = members
        )
        return execMultipartRequest(
            body = body,
            endpoint = assembleProjectsEndpointPath()
        )
    }

    /**
     * Function to execute the request to edit an existing project
     *
     * @param project: the project to edit
     * @param logoPic: the project logo
     * @param projectTitle: the title of the project
     * @param members: the members of the project
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}", method = POST)
    fun editProject(
        project: Project,
        logoPic: String?,
        projectTitle: String,
        members: List<String>
    ) : JSONObject {
        return editProject(
            projectId = project.id,
            logoPic = logoPic,
            projectTitle = projectTitle,
            members = members
        )
    }

    /**
     * Function to execute the request to edit an existing project
     *
     * @param projectId: the identifier of the project
     * @param logoPic: the project logo
     * @param projectTitle: the title of the project
     * @param members: the members of the project
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}", method = POST)
    fun editProject(
        projectId: String,
        logoPic: String?,
        projectTitle: String,
        members: List<String>
    ) : JSONObject {
        val body = createProjectPayload(
            logoPic = if (logoPic != null)
                File(logoPic)
            else
                null,
            projectTitle = projectTitle,
            members = members
        )
        return execMultipartRequest(
            body = body,
            endpoint = assembleProjectsEndpointPath(projectId)
        )
    }

    /**
     * Function to create the payload for the project requests
     *
     * @param logoPic: the project logo
     * @param projectTitle: the title of the project
     * @param members: the members of the project
     *
     * @return the payload of the request as [MultipartBody]
     */
    private fun createProjectPayload(
        logoPic: File?,
        projectTitle: String,
        members: List<String>
    ) : MultipartBody {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                NAME_KEY,
                projectTitle
            )
        if(logoPic != null) {
            body.addFormDataPart(
                LOGO_URL_KEY,
                logoPic.name,
                logoPic.readBytes().toRequestBody("*/*".toMediaType())
            )
        }
        body.addFormDataPart(
            PROJECT_MEMBERS_KEY,
            JSONArray(members).toString(),
        )
        return body.build()
    }

    /**
     * Function to execute the request to get an existing project
     *
     * @param projectId: the project identifier
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}", method = GET)
    fun getProject(
        projectId: String
    ) : JSONObject {
        return execGet(
            endpoint = assembleProjectsEndpointPath(projectId)
        )
    }

    /**
     * Function to execute the request to add new members to a project
     *
     * @param projectId: the project identifier
     * @param invitedMembers: the mailing list of the members to add with their role
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/addMembers", method = PUT)
    fun addMembers(
        projectId: String,
        invitedMembers: SnapshotStateList<Pair<String, Role>>,
    ) : JSONObject {
        val payload = Params()
        val jMembers = JSONArray()
        invitedMembers.forEach { member ->
            val jMember = JSONObject()
            jMember.put(EMAIL_KEY, member.first)
            jMember.put(ROLE_KEY, member.second)
            jMembers.put(jMember)
        }
        payload.addParam(PROJECT_MEMBERS_KEY, jMembers)
        return execPut(
            endpoint = assembleProjectsEndpointPath(projectId + ADD_MEMBERS_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to join in a project by the identifier
     *
     * @param id: the identifier of the joining qrcode used
     * @param email: the email of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param password: the password of the user
     * @param role: the role of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/projects/join", method = POST)
    @Wrapper
    fun joinWithId(
        id: String,
        email: String,
        name: String,
        surname: String,
        password: String,
        role: Role
    ) : JSONObject {
        val payload = Params()
        payload.addParam(IDENTIFIER_KEY, id)
        return join(
            payload = payload,
            email = email,
            name = name,
            surname = surname,
            password = password,
            role = role
        )
    }

    /**
     * Function to execute the request to join in a project using a textual join code
     *
     * @param joinCode: the textual join code to use
     * @param email: the email of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param password: the password of the user
     * @param role: the role of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/projects/join", method = POST)
    @Wrapper
    fun joinWithCode(
        joinCode: String,
        email: String,
        name: String,
        surname: String,
        password: String,
        role: Role
    ) : JSONObject {
        val payload = Params()
        payload.addParam(JOIN_CODE_KEY, joinCode)
        return join(
            payload = payload,
            email = email,
            name = name,
            surname = surname,
            password = password,
            role = role
        )
    }

    /**
     * Function to execute the request to join in a project
     *
     * @param payload: the payload to send with the request
     * @param email: the email of the user
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param password: the password of the user
     * @param role: the role of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/projects/join", method = POST)
    private fun join(
        payload: Params,
        email: String,
        name: String,
        surname: String,
        password: String,
        role: Role
    ) : JSONObject {
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        payload.addParam(NAME_KEY, name)
        payload.addParam(SURNAME_KEY, surname)
        payload.addParam(PASSWORD_KEY, password)
        payload.addParam(ROLE_KEY, role)
        return execPost(
            endpoint = "$PROJECTS_KEY$JOIN_ENDPOINT",
            payload = payload
        )
    }

    /**
     * Function to execute the request to mark a member of the project as tester
     *
     * @param projectId: the project identifier
     * @param memberId: the identifier of the member to mark as tester
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/markAsTester", method = PATCH)
    fun markAsTester(
        projectId: String,
        memberId: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(MEMBER_IDENTIFIER_KEY, memberId)
        return execPatch(
            endpoint = assembleProjectsEndpointPath(projectId + MARK_MEMBER_AS_TESTER_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to remove a member from a project
     *
     * @param projectId: the project identifier
     * @param memberId: the identifier of the member to remove
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/removeMember", method = PATCH)
    fun removeMember(
        projectId: String,
        memberId: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(MEMBER_IDENTIFIER_KEY, memberId)
        return execPatch(
            endpoint = assembleProjectsEndpointPath(projectId + REMOVE_MEMBER_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to leave from a project
     *
     * @param projectId: the project identifier
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}/leave", method = DELETE)
    fun leaveProject(
        projectId: String
    ) : JSONObject {
        return execDelete(
            endpoint = assembleProjectsEndpointPath(projectId + LEAVE_ENDPOINT),
        )
    }

    /**
     * Function to execute the request to delete a project
     *
     * @param projectId: the project identifier
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/{id}/projects/{projectId}", method = DELETE)
    fun deleteProject(
        projectId: String
    ) : JSONObject {
        return execDelete(
            endpoint = assembleProjectsEndpointPath(projectId),
        )
    }

    /**
     * Function to assemble the endpoint to make the request to the projects controller
     *
     * @param endpoint: the endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
    private fun assembleProjectsEndpointPath(
        endpoint: String = ""
    ): String {
        var vEndpoint: String = endpoint
        if(endpoint.isNotEmpty() && !endpoint.startsWith("/"))
            vEndpoint = "/$endpoint"
        return "$userId/$PROJECTS_KEY$vEndpoint"
    }

    /**
     * Function to execute the request to add a new release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseVersion: the version for the release
     * @param releaseNotes: the notes attached to the release
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases", method = POST)
    fun addRelease(
        projectId: String,
        releaseVersion: String,
        releaseNotes: String
    ) : JSONObject {
        val payload = createReleasePayload(
            releaseVersion = releaseVersion,
            releaseNotes = releaseNotes
        )
        return execPost(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to edit an existing release
     *
     * @param project: the project where the release is attached
     * @param release: the release to edit
     * @param releaseVersion: the version for the release
     * @param releaseNotes: the notes attached to the release
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = PATCH)
    fun editRelease(
        project: Project,
        release: Release,
        releaseVersion: String,
        releaseNotes: String
    ) : JSONObject {
        return editRelease(
            projectId = project.id,
            releaseId = release.id,
            releaseVersion = releaseVersion,
            releaseNotes = releaseNotes
        )
    }

    /**
     * Function to execute the request to edit an existing release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier of the release to edit
     * @param releaseVersion: the version for the release
     * @param releaseNotes: the notes attached to the release
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = PATCH)
    fun editRelease(
        projectId: String,
        releaseId: String,
        releaseVersion: String,
        releaseNotes: String
    ) : JSONObject {
        val payload = createReleasePayload(
            releaseVersion = releaseVersion,
            releaseNotes = releaseNotes
        )
        return execPatch(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            ),
            payload = payload
        )
    }

    /**
     * Function to create the payload for the release requests
     *
     * @param releaseVersion: the version for the release
     * @param releaseNotes: the notes attached to the release
     *
     * @return the payload of the request as [Params]
     */
    private fun createReleasePayload(
        releaseVersion: String,
        releaseNotes: String
    ) : Params {
        val payload = Params()
        payload.addParam(RELEASE_VERSION_KEY, releaseVersion)
        payload.addParam(RELEASE_NOTES_KEY, releaseNotes)
        return payload
    }

    /**
     * Function to execute the request to get an existing release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to get
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = GET)
    fun getRelease(
        projectId: String,
        releaseId: String
    ) : JSONObject {
        return execGet(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    /**
     * Function to execute the request to upload assets to a release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where upload the assets
     * @param assets: the list of the assets to upload
     * @param comment: the comment about the assets uploaded
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = POST)
    fun uploadAsset(
        projectId: String,
        releaseId: String,
        assets: List<File>,
        comment: String
    ) : JSONObject {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
        assets.forEach { asset ->
            body.addFormDataPart(
                ASSETS_UPLOADED_KEY,
                asset.name,
                asset.readBytes().toRequestBody("*/*".toMediaType())
            )
            body.addFormDataPart(
                COMMENT_KEY,
                comment
            )
        }
        return execMultipartRequest(
            body = body.build(),
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                endpoint = UPLOAD_ASSETS_ENDPOINT
            )
        )
    }

    /**
     * Function to execute the request to approve the last assets uploaded
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the assets
     * @param eventId: the event identifier to comment
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
        method = POST
    )
    fun approveAssets(
        projectId: String,
        releaseId: String,
        eventId: String,
    ): JSONObject {
        return commentAssets(
            projectId = projectId,
            releaseId = releaseId,
            eventId = eventId,
            releaseStatus = Approved
        )
    }

    /**
     * Function to execute the request to reject the last assets uploaded
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the assets
     * @param eventId: the event identifier to comment
     * @param reasons: the reasons of the rejections
     * @param tags: list of tags attached to the rejection
     *
     * @return the result of the request as [JSONObject]
     */
    @Wrapper
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
        method = POST
    )
    fun rejectAssets(
        projectId: String,
        releaseId: String,
        eventId: String,
        reasons: String,
        tags: List<ReleaseTag>
    ): JSONObject {
        return commentAssets(
            projectId = projectId,
            releaseId = releaseId,
            eventId = eventId,
            releaseStatus = Rejected,
            reasons = reasons,
            tags = tags
        )
    }

    /**
     * Function to execute the request to comment the last assets uploaded
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where comment the assets
     * @param eventId: the event identifier to comment
     * @param releaseStatus: the status of the release [[ReleaseStatus.Approved] | [ReleaseStatus.Rejected]]
     * @param reasons: the reasons of the rejections
     * @param tags: list of tags attached to the rejection
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/comment/{asset_uploading_event_id}",
        method = POST
    )
    private fun commentAssets(
        projectId: String,
        releaseId: String,
        eventId: String,
        releaseStatus: ReleaseStatus,
        reasons: String? = null,
        tags: List<ReleaseTag>? = null
    ) : JSONObject {
        val payload = Params()
        payload.addParam(RELEASE_EVENT_STATUS_KEY, releaseStatus)
        if(reasons != null)
            payload.addParam(REASONS_KEY, reasons)
        if (tags != null)
            payload.addParam(TAGS_KEY, JSONArray(tags))
        return execPost(
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                extraId = eventId,
                endpoint = COMMENT_ASSET_ENDPOINT
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to fill a rejected tag
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier where fill the rejected tag
     * @param eventId: the rejected event identifier where the rejected tag is attached
     * @param tagId: the rejected tag to fill
     * @param comment: the comment to attach at the rejected tag
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/events/{release_event_id}/tags/{release_tag_id}",
        method = PUT
    )
    fun fillRejectedTag(
        projectId: String,
        releaseId: String,
        eventId: String,
        tagId: String,
        comment: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(COMMENT_KEY, comment)
        return execPut(
            endpoint = assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                extraId = "$eventId/$TAGS_KEY/$tagId",
                endpoint = EVENTS_ENDPOINT
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to promote a release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to update
     * @param releaseStatus: the status of the release to set
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/promote", method = PATCH)
    fun promoteRelease(
        projectId: String,
        releaseId: String,
        releaseStatus: ReleaseStatus
    ) : JSONObject {
        val payload = Params()
        payload.addParam(RELEASE_STATUS_KEY, releaseStatus)
        return execPatch(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                endpoint = PROMOTE_RELEASE_ENDPOINT
            ),
            payload = payload
        )
    }

    /**
     * Function to execute the request to create a release report
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier from create the report
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(
        path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}/createReport",
        method = GET
    )
    fun createReportRelease(
        projectId: String,
        releaseId: String
    ) : JSONObject {
        return execGet(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId,
                endpoint = CREATE_REPORT_ENDPOINT
            )
        )
    }

    /**
     * Function to execute the request to delete a release
     *
     * @param projectId: the project identifier where the release is attached
     * @param releaseId: the release identifier to delete
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/{id}/projects/{project_id}/releases/{release_id}", method = DELETE)
    fun deleteRelease(
        projectId: String,
        releaseId: String
    ) : JSONObject {
        return execDelete(
            endpoint =  assembleReleasesEndpointPath(
                projectId = projectId,
                releaseId = releaseId
            )
        )
    }

    /**
     * Function to assemble the endpoint to make the request to the releases controller
     *
     * @param projectId: the project identifier
     * @param releaseId: the release identifier
     * @param extraId: an extra identifier to insert in the path
     * @param endpoint: the endpoint path of the url

     * @return an endpoint to make the request as [String]
     */
    private fun assembleReleasesEndpointPath(
        projectId: String,
        releaseId: String = "",
        extraId: String = "",
        endpoint: String = ""
    ): String {
        var vReleaseId: String = releaseId
        if(releaseId.isNotEmpty())
            vReleaseId = "/$releaseId"
        var vExtraId: String = extraId
        var vEndpoint: String = endpoint
        if(endpoint.isNotEmpty() && !endpoint.startsWith("/"))
            vEndpoint = "/$endpoint"
        if(extraId.isNotEmpty() && !extraId.startsWith("/") && !vEndpoint.endsWith("/"))
            vExtraId = "/$vExtraId"
        return assembleProjectsEndpointPath(projectId) + "/$RELEASES_KEY$vReleaseId$vEndpoint$vExtraId"
    }

}
