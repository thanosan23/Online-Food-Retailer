package ca.uwaterloo.cs.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun Form(state: FormState, fields: List<Field>){
    state.fields = fields

    Column (verticalArrangement = Arrangement.spacedBy(10.dp)) {
        fields.forEach {
            it.Content()
        }
    }
}