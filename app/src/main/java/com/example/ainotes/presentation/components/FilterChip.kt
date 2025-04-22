package com.example.ainotes.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.linguareader.R

@Composable
fun FilterChip(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false
) {
    val background = if (selected) colorResource(id = R.color.blue) else Color.White
    val contentColor = if (selected) Color.White else Color.Black
    val borderColor = if (selected) colorResource(id = R.color.blue) else Color.LightGray

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = background,
        border = BorderStroke(2.dp, borderColor),
        contentColor = contentColor,
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = 14.sp),
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}