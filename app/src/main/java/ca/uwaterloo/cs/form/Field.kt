package ca.uwaterloo.cs.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties

class Field(
    val name: String,
    val initValue: String = "",
    val prompt: String = "",
    val label: String = "",
    val validators: List<Validator>,
    val inputType: KeyboardType = KeyboardType.Text,
    val formatter: VisualTransformation = NoTransformation(),
    val readOnly: Boolean = false,
    private val dropdownList: List<String> = emptyList(),
    private val onChange: (String) -> Unit = {}
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
        var openMenu by remember { mutableStateOf(false) }
        var dropdownMenuSize by remember { mutableStateOf(Size.Zero) }
        val icon = if (openMenu)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
                    .padding(10.dp)
                    .onGloballyPositioned { coordinates ->
                        dropdownMenuSize = coordinates.size.toSize()
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType = inputType,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
//            keyboardActions = KeyboardActions(
//                onDone = {keyboardController?.hide()}),
                //colors = TextFieldDefaults.textFieldColors(cursorColor = Color.Black),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = Color.Black,
                    backgroundColor = Color.LightGray
                ),
                visualTransformation = formatter,
                onValueChange = { value ->
                    hideError()
                    text = value
                    onChange(value)
                },
                readOnly = readOnly,
                trailingIcon = if (dropdownList.isNotEmpty()) {
                    {
                        Icon(icon, "Description", Modifier.clickable { openMenu = !openMenu })
                    }
                } else null,
            )
            val filteredDropDown = dropdownList.filter { it.startsWith(text, ignoreCase = true) }
            if (filteredDropDown.isNotEmpty()) {
                DropdownMenu(
                    expanded = openMenu,
                    onDismissRequest = {
                        openMenu = false
                    },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { dropdownMenuSize.width.toDp() }),
                    properties = PopupProperties(focusable = false)
                ) {
                    filteredDropDown.forEach { label ->
                        DropdownMenuItem(onClick = {
                            text = label
                            openMenu = false
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }
        }
    }

    fun setValue(newValue: String) {
        this.text = newValue
    }

    fun getValue(): String {
        return this.text
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
                is IsNumber -> {
                    try {
                        text.toDouble()
                        return@map true
                    } catch (e: NumberFormatException) {
                        showError(it.message)
                        return@map false
                    }
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
                    return offset + (offset - 1) / 3
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return offset / 4 * 3 + offset % 4
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
