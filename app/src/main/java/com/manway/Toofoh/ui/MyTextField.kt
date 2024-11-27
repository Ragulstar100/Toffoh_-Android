package com.manway.toffoh.admin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.manway.Toofoh.data.ErrorInfo
import com.manway.Toofoh.data.ErrorState

@Composable
fun MyOutlinedTextField(value: String, onValueChange: (String) -> Unit, label: String, errorList:List<String>, errorIndex:Int, readOnly: Boolean = false, interact:Boolean=true, trailingIcon: @Composable (() -> Unit)= {}, leadingIcon: @Composable (() -> Unit)= {}, keyboardType: KeyboardType = KeyboardType.Text, keyboardActions: KeyboardActions = KeyboardActions.Default, modifier: Modifier = Modifier) {
    val errorMsg = errorList[errorIndex]
    var _value by remember {
        mutableStateOf(value)
    }
    Column(modifier) {
        OutlinedTextField(
            value = _value,
            onValueChange ={
                _value=it
                onValueChange(_value)
            },
            label = { Text(label) },
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            supportingText = {
                Text(text = errorMsg, color = Color.Red)
            },
            shape = MaterialTheme.shapes.small,
            leadingIcon = leadingIcon,
            keyboardActions = keyboardActions,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = modifier
        )


    }
}

@Composable
fun MyOutlinedTextField(value: String, onValueChange: (String) -> Unit, label: String, errorInfo: ErrorInfo?, errorState: ErrorState, readOnly: Boolean = false, trailingIcon: @Composable (() -> Unit)= {}, leadingIcon: @Composable (() -> Unit)= {}, keyboardType: KeyboardType = KeyboardType.Text, keyboardActions: KeyboardActions = KeyboardActions.Default, modifier: Modifier = Modifier) {
    val isError = errorState.error && errorState.interact && errorInfo != null && errorInfo.field == label
    Row {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            supportingText = {
            Text(
                text = errorInfo!!.message,
                color = Color.Red
            )
            },
            label = { Text(label) },
            shape = MaterialTheme.shapes.small,
            isError = isError,
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            keyboardActions = keyboardActions,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = modifier
        )

    }
}


