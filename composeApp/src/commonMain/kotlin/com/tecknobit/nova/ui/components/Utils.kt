@file:OptIn(ExperimentalResourceApi::class, ExperimentalResourceApi::class)

package com.tecknobit.nova

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.nova.ui.screens.Splashscreen.Companion.activeLocalSession
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.NovaUser.DEFAULT_PROFILE_PIC
import com.tecknobit.novacore.records.project.Project
import com.tecknobit.novacore.records.release.Release.ReleaseStatus
import com.tecknobit.novacore.records.release.Release.ReleaseStatus.*
import com.tecknobit.novacore.records.release.events.RejectedTag
import com.tecknobit.novacore.records.release.events.ReleaseEvent
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent
import com.tecknobit.novacore.records.release.events.ReleaseStandardEvent.*
import nova.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

/**
 * Function to display an image as logo
 *
 * @param size: the size of the logo, default value 55.[dp]
 * @param url: the url of the image to display
 */
@Composable
fun Logo(
    url: String,
    size: Dp = 55.dp
) {
    AsyncImage(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .size(size),
        imageLoader = imageLoader,
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            .crossfade(true)
            .crossfade(500)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        error = painterResource(Res.drawable.logo)
    )
}

/**
 * Function to create a badge for a [ReleaseStatus]
 *
 * @param releaseStatus: the status to use to create the badge
 * @param paddingStart: the padding from the start, default value 10.[dp]
 */
@Composable
fun ReleaseStatusBadge(
    releaseStatus: ReleaseStatus,
    paddingStart: Dp = 10.dp
) {
    OutlinedCard (
        modifier = Modifier
            .padding(
                start = paddingStart
            )
            .requiredWidthIn(
                min = 65.dp,
                max = 100.dp
            )
            .wrapContentWidth()
            .height(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = releaseStatus.createColor()
        )
    ) {
        ReleaseStatusBadgeContent(
            releaseStatus = releaseStatus
        )
    }
}

/**
 * Function to create a badge for a [ReleaseStatus]
 *
 * @param releaseStatus: the status to use to create the badge
 * @param paddingStart: the padding from the start, default value 10.[dp]
 * @param onClick: the action to execute when the badge is clicked
 */
@Composable
fun ReleaseStatusBadge(
    releaseStatus: ReleaseStatus,
    paddingStart: Dp = 10.dp,
    onClick: () -> Unit
) {
    OutlinedCard (
        modifier = Modifier
            .padding(
                start = paddingStart
            )
            .requiredWidthIn(
                min = 65.dp,
                max = 100.dp
            )
            .wrapContentWidth()
            .height(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = releaseStatus.createColor()
        ),
        onClick = onClick
    ) {
        ReleaseStatusBadgeContent(
            releaseStatus = releaseStatus
        )
    }
}

/**
 * Function to display the content of the [ReleaseStatusBadge]
 *
 * @param releaseStatus: the status to use to create the badge
 */
@Composable
private fun ReleaseStatusBadgeContent(
    releaseStatus: ReleaseStatus
) {
    Column (
        modifier = Modifier
            .padding(
                start = 10.dp,
                end = 10.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = releaseStatus.name,
            fontWeight = FontWeight.Bold,
            color = releaseStatus.createColor()
        )
    }
}

/**
 * Function to create a specific color from the [ReleaseStatus]
 *
 * @return the specific color as [Color]
 */
fun ReleaseStatus.createColor(): Color {
    return fromHexToColor(color)
}

/**
 * Function to create a badge for a [RejectedTag]
 *
 * @param tag: the tag to use to create the badge
 * @param isLastEvent: whether the tag is placed in the last event occurred in the release
 * @param onClick: the action to execute when the badge is clicked
 */
@Composable
fun ReleaseTagBadge(
    tag: RejectedTag,
    isLastEvent: Boolean,
    onClick: () -> Unit = {}
) {
    val modifier = Modifier
        .requiredWidthIn(
            min = 45.dp,
            max = 140.dp
        )
        .height(35.dp)
    val isAdded = tag.comment != null && tag.comment.isNotEmpty()
    val tagColor = tag.tag.createColor()
    val colors = CardDefaults.cardColors(
        containerColor = if(isAdded)
            tagColor
        else
            Color.White
    )
    val textColor = if(!isAdded)
        tagColor
    else
        Color.White
    val border = BorderStroke(
        width = 1.dp,
        color = textColor
    )
    if(((activeLocalSession.isVendor && tag.comment.isNullOrEmpty()) || !isLastEvent)) {
        OutlinedCard(
            modifier = modifier,
            colors = colors,
            border = border
        ) {
            ReleaseTagContent(
                tag = tag.tag,
                textColor = textColor
            )
        }
    } else {
        OutlinedCard(
            modifier = modifier,
            colors = colors,
            border = border,
            onClick = onClick
        ) {
            ReleaseTagContent(
                tag = tag.tag,
                textColor = textColor
            )
        }
    }
}

/**
 * Function to display the content of the [ReleaseTagBadge]
 *
 * @param tag: the tag to use to create the badge
 * @param textColor: the color of the text
 */
@Composable
private fun ReleaseTagContent(
    tag: ReleaseEvent.ReleaseTag,
    textColor: Color
) {
    Column (
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                start = 10.dp,
                end = 10.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = tag.name,
            color = textColor,
            fontSize = 14.sp
        )
    }
}

/**
 * Function to create a specific color from the [ReleaseTag]
 *
 * @return the specific color as [Color]
 */
fun ReleaseEvent.ReleaseTag.createColor(): Color {
    return fromHexToColor(color)
}

/**
 * Function to get the specific message for a [ReleaseStandardEvent]
 *
 * @return the specific message as [Int]
 */
@ExperimentalResourceApi
fun ReleaseStandardEvent.getMessage(): StringResource {
    return when(this.status) {
        Approved -> Res.string.approved_timeline_message
        Alpha -> Res.string.alpha_timeline_message
        Beta -> Res.string.beta_timeline_message
        Latest -> Res.string.latest_timeline_message
        else -> Res.string.app_name
    }
}

/**
 * **customerColor** -> color for the [Role.Customer]
 */
private val customerColor = Alpha.createColor()

/**
 * **vendorColor** -> color for the [Role.Vendor]
 */
private val vendorColor = Beta.createColor()

/**
 * Function to create a badge for a [Role]
 *
 * @param background: the background color to use, default is [gray_background]
 * @param role: the role to use to create the badge
 */
@Composable
fun UserRoleBadge(
    background: Color = gray_background,
    role: NovaUser.Role
) {
    val badgeColor = if(role == NovaUser.Role.Customer)
        customerColor
    else
        vendorColor
    OutlinedCard (
        modifier = Modifier
            .requiredWidthIn(
                min = 65.dp,
                max = 100.dp
            )
            .wrapContentWidth()
            .height(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        border = BorderStroke(
            width = 1.dp,
            color = badgeColor
        )
    ) {
        Column (
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = role.name,
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
        }
    }
}

/**
 * Function to create a badge for a [Role]
 *
 * @param role: the role to use to create the badge
 * @param selected: whether the badge is selected or not
 * @param onClick: the action to execute when the badge is clicked
 */
@Composable
fun UserRoleBadge(
    role: NovaUser.Role,
    selected: MutableState<Boolean>,
    onClick: () -> Unit
) {
    val badgeColor = if(role == NovaUser.Role.Customer)
        customerColor
    else
        vendorColor
    OutlinedCard (
        modifier = Modifier
            .requiredWidthIn(
                min = 65.dp,
                max = 100.dp
            )
            .wrapContentWidth()
            .height(35.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(selected.value)
                badgeColor
            else
                gray_background
        ),
        border = BorderStroke(
            width = 1.dp,
            color = badgeColor
        ),
        onClick = onClick
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    end = 10.dp
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = role.name,
                fontWeight = FontWeight.Bold,
                color = if(selected.value)
                    Color.White
                else
                    badgeColor
            )
        }
    }
}

/**
 * Method to transform a [Color] from an hex [String]
 * @param hex: hex value to transform
 *
 * @return color as [Color]
 */
private fun fromHexToColor(hex: String): Color {
    return Color(("ff" + hex.removePrefix("#").lowercase()).toLong(16))
}

/**
 * Function to get the complete default profile pic url with the [activeLocalSession]'s host
 *
 * No-any params required
 *
 * @return the default profile pic path complete url to display as [String]
 *
 */
fun getDefProfilePic(): String {
    return activeLocalSession.hostAddress + "/" + DEFAULT_PROFILE_PIC
}

/**
 * Function to assemble the project logo url complete (with the current [activeLocalSession].hostAddress)
 * to display
 *
 * @param project: the project from get the logo url path
 *
 * @return the project logo complete url to display as [String]
 *
 */
fun getProjectLogoUrl(
    project: Project
): String {
    return activeLocalSession.hostAddress + "/" + project.logoUrl
}

/**
 * Function to assemble the profile pic url complete (with the current [activeLocalSession].hostAddress)
 * to display
 *
 * @param member: the member from get the profile pic url path
 *
 * @return the profile pic complete url to display as [String]
 *
 */
fun getMemberProfilePicUrl(
    member: NovaUser
): String {
    return activeLocalSession.hostAddress + "/" + member.profilePic
}

/**
 * Function to assemble the report from get the complete url path (with the current [activeLocalSession].hostAddress)
 * to display
 *
 * @param reportUrl: the report from get the complete url path
 *
 * @return the report from get the complete url path to display as [String]
 *
 */
fun getReportUrl(
    reportUrl: String
): String {
    return activeLocalSession.hostAddress + "/" + reportUrl
}

/**
 * Function to assemble the asset url path (with the current [activeLocalSession].hostAddress)
 * to display
 *
 * @param asset: the asset from get the complete url path
 *
 * @return the asset from get the complete url path to display as [String]
 *
 */
fun getAssetUrl(
    asset: String
): String {
    return activeLocalSession.hostAddress + "/" + asset
}

