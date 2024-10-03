package com.tecknobit.nova.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection.Companion.Next
import androidx.compose.ui.focus.FocusDirection.Companion.Previous
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
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

/**
 * **focusManager** -> the focus manager
 */
private lateinit var focusManager: FocusManager

/**
 * Component to allow the user to insert a split text such OTP codes, PIN or any other texts of this type
 *
 * @param columnModifier: the modifier to apply to the [Column] container
 * @param rowModifier: the modifier to apply to the [LazyRow] container
 * @param splitsTextState: the state used to manage this component
 * @param spacingBetweenBoxes: the spacing between the boxes
 * @param boxShape: the shape to apply to the [SplitBox]
 * @param boxTextStyle: the text style to use for the [SplitBox]'s text
 * @param infoText: the text, if passed, to display as information about the text to insert in the
 * component
 */
@Composable
@NonRestartableComposable
@TestOnly
fun SplitText(
    columnModifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
    splitsTextState: SplitTextState,
    spacingBetweenBoxes: Dp = 10.dp,
    boxShape: Shape = CardDefaults.shape,
    boxTextStyle: TextStyle = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    infoText: String? = null
) {
    focusManager = LocalFocusManager.current
    splitsTextState.CreateSlices()
    Column(
        modifier = columnModifier
    ) {
        LazyRow(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacingBetweenBoxes)
        ) {
            itemsIndexed(
                items = splitsTextState.textSlices
            ) { index, textSlice ->
                SplitBox(
                    currentTextSlices = splitsTextState.textSlices,
                    boxShape = boxShape,
                    boxTextStyle = boxTextStyle,
                    textSlice = textSlice,
                    currentBox = index
                )
            }
        }
        infoText?.let {
            Text(
                text = infoText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Single component to allow the user to a slice of the complete text of the [SplitText] component
 *
 * @param currentTextSlices: the array container of the each parts of the split text
 * @param boxShape: the shape to apply to the [SplitBox]
 * @param boxTextStyle: the text style to use for the [SplitBox]'s text
 * @param textSlice: the slice of the complete text to fill
 * @param currentBox: the index of the current box where the focus is applied
 */
@Composable
@NonRestartableComposable
@TestOnly
// TODO: ALLOW MORE CUSTOMIZATION IN THE OFFICIAL COMPONENT 
private fun SplitBox(
    currentTextSlices: ArrayList<MutableState<String>>,
    boxShape: Shape,
    boxTextStyle: TextStyle,
    textSlice: MutableState<String>,
    currentBox: Int
) {
    val isLast = currentBox == currentTextSlices.lastIndex
    val keyboardManager = LocalSoftwareKeyboardController.current
    val selectedBorderColor = MaterialTheme.colorScheme.inversePrimary
    var selectedBoxColor by remember { mutableStateOf(Color.Transparent) }
    var hasFocus by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .onKeyEvent { event ->
                if (event.key == Key.Backspace) {
                    textSlice.value = ""
                    if (currentBox > 0) {
                        if (!isLast)
                            currentTextSlices[currentBox - 1].value = ""
                        focusManager.moveFocus(Previous)
                    }
                }
                false
            }
            .onFocusChanged {
                hasFocus = it.hasFocus
                selectedBoxColor = if (hasFocus)
                    selectedBorderColor
                else
                    Color.Transparent
            }
            .border(
                color = selectedBoxColor,
                width = 2.dp,
                shape = boxShape
            )
            .size(50.dp),
        shape = boxShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (hasFocus)
                3.dp
            else
                1.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                modifier = Modifier
                    .align(Alignment.Center),
                value = textSlice.value,
                onValueChange = { slice ->
                    val sliceLength = slice.length
                    if (sliceLength == currentTextSlices.size) {
                        pasteSlices(
                            splits = sliceLength,
                            currentTextSlices = currentTextSlices,
                            slice = slice
                        )
                    } else {
                        if (slice.isEmpty())
                            textSlice.value = slice
                        else {
                            if (!isLast) {
                                textSlice.value = slice.first().toString()
                                focusManager.moveFocus(Next)
                            } else
                                textSlice.value = slice.last().toString()
                        }
                    }
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

/**
 * Function to automatically all the [SplitBox] when the user copy the text to insert in the [SplitText]
 *
 * @param splits: the number of splits used to create the [SplitText] component
 * @param currentTextSlices: the array container of the each parts of the split text
 * @param slice: the complete text requested to copy in the component
 */
private fun pasteSlices(
    splits: Int,
    currentTextSlices: ArrayList<MutableState<String>>,
    slice: String
) {
    repeat(splits) { offset ->
        currentTextSlices[offset].value = slice.drop(offset).first().toString()
    }
}