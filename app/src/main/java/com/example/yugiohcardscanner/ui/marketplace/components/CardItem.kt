package com.example.yugiohcardscanner.ui.marketplace.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.yugiohcardscanner.data.models.*
import com.example.yugiohcardscanner.ui.marketplace.components.*

@Composable
fun CardItem(
    card: CardData,
    onAddToCollection: (CardData) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(280.dp) // ✅ the agreed height (adjust if needed)
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
                Log.d("CardItem", "Loading image from: ${card.imageUrl}")

                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = card.name,
                    modifier = Modifier
                        .height(150.dp) // ✅ controlled height
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

            // Bottom row: Price + Add button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${"%.2f".format(card.marketPrice)}",
                    color = Color(0xFFD0D0D0), // match price color to rarity font
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                IconButton(
                    onClick = { onAddToCollection(card) },
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF2C2C2C), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Collection",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}



