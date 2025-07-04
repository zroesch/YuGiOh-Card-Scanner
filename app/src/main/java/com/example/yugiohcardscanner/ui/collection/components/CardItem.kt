package com.example.yugiohcardscanner.ui.collection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.example.yugiohcardscanner.data.models.CardData

/**
 * Composable function to display a single card item within the user's collection.
 *
 * This function is responsible for rendering a card's details, including its
 * image, name, set information, rarity, number, market price, and the count
 * of how many of that card the user has. It also includes a button to remove
 * the card from the collection.
 * The image loading logic prioritizes a local `storageUrl` if available,
 * otherwise it checks if the `imageUrl` points to a local drawable resource.
 * If neither is true, it attempts to load the `imageUrl` as a network resource.
 *
 * @param card The [CardData] object containing the card's information.
 * @param onRemoveFromCollection Callback function to be executed when the
 *   "Remove from Collection" button is clicked.
 * @param modifier Modifier to be applied to the card container.
 */
@Composable
fun CardItem(
    card: CardData,
    onRemoveFromCollection: (CardData) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(280.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Image
                val context = LocalContext.current
                val imageRequest = if (card.storageUrl == null && card.imageUrl?.startsWith("drawable/") == true) {
                    val drawableName = card.imageUrl.substringAfter("drawable/")
                    val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                    ImageRequest.Builder(LocalPlatformContext.current)
                        .data(resourceId)
                        .build()
                } else {
                    ImageRequest.Builder(LocalPlatformContext.current)
                        .data(card.storageUrl ?: card.imageUrl) // Prioritize storageUrl if available
                        .build()
                }

                AsyncImage(
                    model = imageRequest,
                    contentDescription = card.name,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Name
                Text(
                    text = card.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Set name
                Text(
                    text = card.setName,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Rarity + Number
                Text(
                    text = "${card.extRarity} • ${card.extNumber}",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Bottom row: Price + Count + Delete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$${"%.2f".format(card.marketPrice)}",
                        color = Color(0xFFD0D0D0),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Count: ${card.count}",
                        color = Color(0xFFD0D0D0),
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = { onRemoveFromCollection(card) },
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF2C2C2C), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from Collection",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}