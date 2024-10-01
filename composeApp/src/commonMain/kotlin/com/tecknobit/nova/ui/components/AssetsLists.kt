package com.tecknobit.nova.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tecknobit.nova.Logo
import com.tecknobit.novacore.records.release.events.AssetUploadingEvent.AssetUploaded
import java.io.File

private const val BYTES_TO_MEGABYTE_RATE = 1_000_000

@Composable
@NonRestartableComposable
fun AssetsToUpload(
    modifier: Modifier = Modifier,
    uploadingAssets: SnapshotStateList<File>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 16.dp
        )
    ) {
        items(
            items = uploadingAssets,
            key = { asset -> asset.absolutePath },
        ) { asset ->
            AssetToUpload(
                uploadingAssets = uploadingAssets,
                asset = asset
            )
            HorizontalDivider()
        }
    }
}

@Composable
@NonRestartableComposable
private fun AssetToUpload(
    uploadingAssets: SnapshotStateList<File>,
    asset: File
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        leadingContent = {
            Logo(
                url = asset.absolutePath
            )
        },
        headlineContent = {
            Text(
                text = asset.name
            )
        },
        supportingContent = {
            Text(
                text = "${(asset.length() / BYTES_TO_MEGABYTE_RATE)} MB"
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

@Composable
@NonRestartableComposable
fun AssetsToDownload(
    modifier: Modifier = Modifier,
    selectionList: SnapshotStateList<AssetUploaded>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 16.dp
        )
    ) {
        items(
            items = selectionList,
            key = { asset -> asset.id }
        ) { asset ->
            AssetToDownload(
                selectionList = selectionList,
                asset = asset
            )
            HorizontalDivider()
        }
    }
}

@Composable
@NonRestartableComposable
private fun AssetToDownload(
    selectionList: SnapshotStateList<AssetUploaded>,
    asset: AssetUploaded
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        leadingContent = {
            Logo(
                url = asset.url
            )
        },
        headlineContent = {
            Text(
                text = asset.name
            )
        },
        trailingContent = {
            AnimatedVisibility(
                visible = selectionList.size > 1
            ) {
                IconButton(
                    onClick = { selectionList.remove(asset) }
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