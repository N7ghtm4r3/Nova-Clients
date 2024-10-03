package com.tecknobit.nova.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tecknobit.nova.ui.theme.gray_background
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.confirm
import nova.composeapp.generated.resources.dismiss
import org.jetbrains.compose.resources.stringResource

/**
 * The dialog where the user can manage the assets to upload or download
 *
 * @param onDismissRequest: the request to execute the the dialog is dismissed
 * @param content: the content of the dialog
 */
@Composable
@NonRestartableComposable
fun AssetsDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier
                .size(
                    width = 400.dp,
                    height = 500.dp
                ),
            shape = RoundedCornerShape(10.dp),
            color = gray_background,
            content = content
        )
    }
}

/**
 * The action available for an [AssetsDialog]
 *
 * @param modifier: the modifier to apply to the [Row] container
 * @param dismissAction: the action to execute when the user dismiss the [AssetsDialog]
 * @param confirmAction: the action to execute when the user confirm
 */
@Composable
@NonRestartableComposable
fun DialogsActions(
    modifier: Modifier,
    dismissAction: () -> Unit,
    confirmAction: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        TextButton(
            onClick = dismissAction
        ) {
            Text(
                text = stringResource(Res.string.dismiss)
            )
        }
        TextButton(
            onClick = confirmAction
        ) {
            Text(
                text = stringResource(Res.string.confirm)
            )
        }
    }
}