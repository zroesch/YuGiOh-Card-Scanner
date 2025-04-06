package com.example.yugiohcardscanner.ui.marketplace.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CollectionButton(
    isInCollection: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isInCollection) Color.Green else Color.Blue
        ),
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
    ) {
        Icon(
            imageVector = if (isInCollection) Icons.Default.Check else Icons.Default.Add,
            contentDescription = "Add to Collection",
            tint = Color.White
        )
    }
}

