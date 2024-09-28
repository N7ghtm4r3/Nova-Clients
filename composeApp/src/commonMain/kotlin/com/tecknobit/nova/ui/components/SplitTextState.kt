package com.tecknobit.nova.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberSplitTextState(
    splits: Int
): SplitTextState {
    val splitTextState = rememberSaveable(
        stateSaver = SplitTextSaver
    ) {
        mutableStateOf(
            SplitTextState(
                splits = splits
            )
        )
    }
    return splitTextState.value
}

class SplitTextState internal constructor(
    val splits: Int,
    val initialSliceValues: List<String> = List(splits) { "" }
) {

    val textSlices = arrayListOf<MutableState<String>>()

    @Composable
    @NonRestartableComposable
    fun CreateSlices() {
        textSlices.clear()
        repeat(splits) { index ->
            textSlices.add(
                remember {
                    mutableStateOf(initialSliceValues[index])
                }
            )
        }
    }

    fun getCompleteText(): String {
        var entireText = ""
        textSlices.forEach { slice ->
            entireText += slice.value
        }
        return entireText
    }

}

object SplitTextSaver : Saver<SplitTextState, Pair<Int, List<String>>> {

    /**
     * Convert the value into a saveable one. If null is returned the value will not be saved.
     */
    override fun SaverScope.save(
        value: SplitTextState
    ): Pair<Int, List<String>> {
        return Pair(
            first = value.splits,
            second = value.initialSliceValues
        )
    }

    /**
     * Convert the restored value back to the original Class. If null is returned the value will
     * not be restored and would be initialized again instead.
     */
    override fun restore(
        value: Pair<Int, List<String>>
    ): SplitTextState {
        return SplitTextState(
            splits = value.first,
            initialSliceValues = value.second
        )
    }

}