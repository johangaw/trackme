package com.trackme.android.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = true,
                role = Role.Checkbox,
            )
            .border(2.dp, if (checked) MaterialTheme.colors.primary else Color.Transparent, RoundedCornerShape(4.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview("on")
@Composable
fun ToggleButtonPreviewOn() {
    ToggleButton(checked = true, onCheckedChange = { /*TODO*/ }) {
        Text("Some text")
    }
}

@Preview("off")
@Composable
fun ToggleButtonPreviewOff() {
    ToggleButton(checked = false, onCheckedChange = { /*TODO*/ }) {
        Text("Some text")
    }
}