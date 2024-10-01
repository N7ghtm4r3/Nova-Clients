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
import com.tecknobit.nova.theme.gray_background
import nova.composeapp.generated.resources.Res
import nova.composeapp.generated.resources.confirm
import nova.composeapp.generated.resources.dismiss
import org.jetbrains.compose.resources.stringResource

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