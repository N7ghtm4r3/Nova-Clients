package com.tecknobit.nova.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import com.tecknobit.nova.Logo
import java.io.File

@Composable
@NonRestartableComposable
fun UploadingAssets(
    uploadingAssets: SnapshotStateList<File>
) {
    LazyColumn {
        items(
            items = uploadingAssets,
            key = { asset -> asset.absolutePath }
        ) { asset ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                leadingContent = {
                    Logo(
                        url = asset.path
                    )
                },
                headlineContent = {
                    Text(
                        text = asset.name
                    )
                },
                supportingContent = {
                    Text(
                        text = asset.length().toString()
                    )
                },
                trailingContent = {
                    AnimatedVisibility(
                        visible = uploadingAssets.size > 1
                    ) {
                        IconButton(
                            onClick = { uploadingAssets.remove(asset) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    }
}