package com.example.yugiohcardscanner.ui.marketplace.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yugiohcardscanner.data.models.SortType

/**
 * Composable function for displaying a bottom sheet for sorting options.
 *
 * This function creates a modal bottom sheet that presents the user with
 * different options for sorting cards, such as by price, name, or rarity.
 *
 * @param currentSort The currently selected sort type.
 * @param onSortSelected Callback function triggered when a sort option is selected.
 * @param onDismiss Callback function triggered when the bottom sheet is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingBottomSheet(
    currentSort: SortType,
    onSortSelected: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = Color.DarkGray
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

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