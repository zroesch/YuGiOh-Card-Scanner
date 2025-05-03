package com.example.yugiohcardscanner.ui.scanner.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.yugiohcardscanner.data.models.CardData
import java.util.Locale

/**
 * Composable function that displays a preview of a scanned card.
 *
 * This composable shows the card's image, name, set extension number, and market price.
 *
 * @param card The [CardData] object containing the card's information.
 * @param onDismiss Callback to be invoked when the card preview is dismissed.
 */
@Composable
fun CardPreview(
    card: CardData,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = card.name,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = card.extNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$${String.format(Locale.US, "%.2f", card.marketPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}