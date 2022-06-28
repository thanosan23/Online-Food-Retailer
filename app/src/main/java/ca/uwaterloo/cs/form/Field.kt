package ca.uwaterloo.cs.form

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

class Field(
    val name: String,
    val initValue: String = "",
    val prompt: String = "",
    val label: String = "",
    val validators: List<Validator>,
    val inputType: KeyboardType = KeyboardType.Text,
    val formatter: VisualTransformation = NoTransformation()
) {
    var text: String by mutableStateOf(initValue)
    var lbl: String by mutableStateOf(label)
    var hasError: Boolean by mutableStateOf(false)

    fun clear() {
        text = ""
    }

    private fun showError(error: String) {
        hasError = true
        lbl = error
    }

    private fun hideError() {
        lbl = label
        hasError = false
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Content() {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = FocusRequester()
        OutlinedTextField(
            value = text,
            isError = hasError,
            label = { Text(text = lbl) },
            placeholder = { Text(text = prompt) },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!it.isFocused) {
                        keyboardController?.hide()
                    }
                }
                .padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = inputType, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
//            keyboardActions = KeyboardActions(
//                onDone = {keyboardController?.hide()}),
            //colors = TextFieldDefaults.textFieldColors(cursorColor = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(cursorColor = Color.Black,
                backgroundColor = Color.LightGray),
            visualTransformation = formatter,
            onValueChange = { value ->
                hideError()
                text = value
            }
        )
    }

    fun validate(): Boolean {
        return validators.map {
            when (it) {
                is Required -> {
                    if (text.isEmpty()) {
                        showError(it.message)
                        return@map false
                    }
                    true
                }
                is NonZero -> {
                    if (text.toDouble() <= 0.0) {
                        showError(it.message)
                        return@map false
                    }
                    true
                }
            }
        }.all { it }
    }
}

class NumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty() || text.length <= 4) {
            return TransformedText(text, OffsetMapping.Identity)
        } else {
            val sb = StringBuilder()
            var counter = -1
            for (ch in text.text.reversed()) {
                counter++
                if (counter == 3) {
                    sb.append(',')
                    counter = 0
                }
                sb.append(ch)

            }

            val formattedText = sb.reverse().toString()

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    println("originalToTransformed")
                    println (offset)
                    val tr = offset + (offset - 1) / 3
                    println(tr)
                    return tr
                }

                override fun transformedToOriginal(offset: Int): Int {
                    println("transformedToOriginal")
                    println (offset)
                    val tr = offset / 4 * 3 + offset % 4
                    println(tr)
                    return tr
                }
            }
            return TransformedText(AnnotatedString(formattedText), offsetMapping)
        }
    }
}

// TODO: price transformation

class NoTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(text, OffsetMapping.Identity)
    }
}
