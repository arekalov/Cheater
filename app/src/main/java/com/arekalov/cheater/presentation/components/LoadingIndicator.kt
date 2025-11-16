package com.arekalov.cheater.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.CircularProgressIndicator
import com.arekalov.cheater.presentation.theme.CheaterTheme

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun LoadingIndicatorPreview() {
    CheaterTheme {
        LoadingIndicator()
    }
}

