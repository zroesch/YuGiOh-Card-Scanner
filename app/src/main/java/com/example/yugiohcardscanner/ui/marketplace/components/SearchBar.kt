package com.example.yugiohcardscanner.ui.marketplace.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.sp


@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSortClick: () -> Unit = {},
    onShowSetsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        // 🔹 Top Row: Camera, Search Field, Sort
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Hook up camera */ }) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            TextField(
                value = query,
                onValueChange = { onQueryChanged(it) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = {
                    Text("Search for products", color = Color.Gray)
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1C1C1C),
                    unfocusedContainerColor = Color(0xFF1C1C1C),
                    disabledContainerColor = Color(0xFF1C1C1C),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSortClick) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = Color.White
                )
            }
        }

        // 🔹 Bottom Row: Show Sets Button aligned to end
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onShowSetsClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text("Show Sets", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}