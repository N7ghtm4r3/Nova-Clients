package com.tecknobit.nova.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.Logo
import com.tecknobit.nova.UserRoleBadge
import com.tecknobit.nova.getMemberProfilePicUrl
import com.tecknobit.nova.theme.gray_background
import com.tecknobit.novacore.records.NovaUser

@Composable
@NonRestartableComposable
fun MemberListItem(
    member: NovaUser,
    trailingContent: @Composable () -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = gray_background
        ),
        leadingContent = {
            Logo(
                url = getMemberProfilePicUrl(member)
            )
        },
        headlineContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "${member.name} ${member.surname}"
                )
                UserRoleBadge(
                    role = member.role
                )
            }
        },
        supportingContent = {
            Text(
                text = member.email
            )
        },
        trailingContent = trailingContent
    )
    HorizontalDivider()
}