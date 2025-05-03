package com.example.yugiohcardscanner.ui.scanner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.ui.scanner.ScannerViewModel
import kotlinx.coroutines.launch
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

/**
 * Composable function to display the review screen for scanned cards.
 *
 * This screen allows users to review the scanned cards and add them to their collection.
 *
 * @param viewModel The [ScannerViewModel] to interact with.
 * @param sharedViewModel The [SharedCardViewModel] to interact with.
 * @param onNavigateBack Callback to navigate back to the previous screen.
 * @param navController The [NavHostController] for navigation.
 * @param onNavigateToCollection Callback to navigate to the collection screen.
 */
@Composable
fun ReviewScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    sharedViewModel: SharedCardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    navController: NavHostController,
    onNavigateToCollection: () -> Unit
) {
    val scannedCards by viewModel.scannedCards
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Review Your Matches",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Adding to: Main",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Green,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(scannedCards) { card ->
                ReviewCardItem(card = card)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${scannedCards.size} matched scans.",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = {
                    scope.launch {
                        sharedViewModel.addCardsToCollection(scannedCards)
                        viewModel.clearScannedCards()
                        onNavigateToCollection()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Portfolio")
            }
        }
    }
}

/**
 * Composable function to display an individual card item in the review screen.
 *
 * @param card The [CardData] object to display.
 */
@Composable
fun ReviewCardItem(card: CardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = card.imageUrl,
                contentDescription = card.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = card.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(text = card.extNumber, color = Color.White.copy(alpha = 0.7f))
                Text(
                    text = "$${String.format("%.2f", card.marketPrice)}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}