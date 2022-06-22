package ca.uwaterloo.cs.form

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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

    @Composable
    fun Content() {
        OutlinedTextField(
            value = text,
            isError = hasError,
            label = { Text(text = lbl) },
            placeholder = { Text(text = prompt) },
            modifier = Modifier.padding(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = inputType),
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
                    if (text.toInt() == 0) {
                        showError(it.message)
                        return@map false
                    }
                    true
                }
            }
        }.all { it }
    }
}

class NoTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.isEmpty() || text.length <= 4) {
            return TransformedText(text, OffsetMapping.Identity)
        } else {
            val sb = StringBuilder()
            var counter = 0
            for (ch in text.text.reversed()) {
                sb.append(ch)
                counter++
                if (counter == 3) {
                    sb.append(',')
                    counter = 0
                }
            }

            val formattedText = sb.reverse().toString()

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return offset + offset / 3 + offset % 3
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return offset / 4 * 3 + offset % 4
                }
            }
            return TransformedText(AnnotatedString(formattedText), offsetMapping)
        }
    }
}

class NumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(text, OffsetMapping.Identity)
    }
}
