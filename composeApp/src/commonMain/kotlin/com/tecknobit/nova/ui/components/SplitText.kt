package com.tecknobit.nova.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection.Companion.Next
import androidx.compose.ui.focus.FocusDirection.Companion.Previous
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.annotations.TestOnly

private lateinit var focusManager: FocusManager

@Composable
@NonRestartableComposable
@TestOnly
fun SplitText(
    modifier: Modifier = Modifier,
    splits: Int,
    spacingBetweenBoxes: Dp = 10.dp,
    boxShape: Shape = CardDefaults.shape,
    boxTextStyle: TextStyle = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
) {
    focusManager = LocalFocusManager.current
    val textSlices = arrayListOf<MutableState<String>>()
    repeat(splits) {
        textSlices.add(remember { mutableStateOf("") })
    }
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacingBetweenBoxes)
    ) {
        itemsIndexed(
            items = textSlices
        ) { index, textSlice ->
            SplitBox(
                currentTextSlices = textSlices,
                boxShape = boxShape,
                boxTextStyle = boxTextStyle,
                textSlice = textSlice,
                currentBox = index
            )
        }
    }
}

@Composable
@NonRestartableComposable
private fun SplitBox(
    currentTextSlices: ArrayList<MutableState<String>>,
    boxShape: Shape,
    boxTextStyle: TextStyle,
    textSlice: MutableState<String>,
    currentBox: Int
) {
    val lastIndex = currentTextSlices.lastIndex
    val isLast = currentBox == lastIndex
    val keyboardManager = LocalSoftwareKeyboardController.current
    Card(
        modifier = Modifier
            .size(50.dp),
        shape = boxShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                modifier = Modifier
                    .onKeyEvent { event ->
                        if (event.key == Key.Backspace) {
                            textSlice.value = ""
                            if (currentBox > 0)
                                focusManager.moveFocus(Previous)
                        }
                        true
                    }
                    .align(Alignment.Center),
                value = textSlice.value,
                onValueChange = {
                    if (textSlice.value.isNotEmpty() && !isLast) {
                        currentTextSlices[currentBox + 1].value = it..ifEmpty {
                            ""
                        }
                        focusManager.moveFocus(Next)
                    } else if (it.isNotEmpty())
                        textSlice.value = it.first().toString()

                    /*if(it.isEmpty()) {
                        textSlice.value = ""
                        if(currentBox > 0)
                            focusManager.moveFocus(Previous)

                    } else {

                    }*/
                },
                textStyle = boxTextStyle,
                cursorBrush = SolidColor(Color.Transparent),
                keyboardOptions = KeyboardOptions(
                    imeAction = if (isLast)
                        ImeAction.Done
                    else
                        ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardManager!!.hide()
                    }
                )
            )
        }
    }
}