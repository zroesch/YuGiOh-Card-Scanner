package com.example.yugiohcardscanner.ui.marketplace.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yugiohcardscanner.data.models.SortType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheet(
    currentSort: SortType,
    onSortSelected: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = Color.DarkGray // ✅ No more `sheetShape`
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Sort By", style = MaterialTheme.typography.titleMedium, color = Color.White)

            val sortOptions = listOf(
                SortType.PRICE_LOW_TO_HIGH to "Price: Low to High",
                SortType.PRICE_HIGH_TO_LOW to "Price: High to Low",
                SortType.NAME_A_TO_Z to "Product Name: A to Z",
                SortType.NAME_Z_TO_A to "Product Name: Z to A",
                SortType.RARITY_LOW_TO_HIGH to "Rarity: Low to High",
                SortType.RARITY_HIGH_TO_LOW to "Rarity: High to Low"
            )

            sortOptions.forEach { (sortType, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSortSelected(sortType) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (sortType == currentSort),
                        onClick = { onSortSelected(sortType) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, color = Color.White)
                }
            }
        }
    }
}



