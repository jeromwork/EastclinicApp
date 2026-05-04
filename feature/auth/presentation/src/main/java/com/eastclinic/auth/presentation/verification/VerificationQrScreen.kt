package com.eastclinic.auth.presentation.verification

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationQrScreen(
    onNavigateToHome: (String) -> Unit,
    viewModel: VerificationQrViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is VerificationQrUiEffect.NavigateToHome -> onNavigateToHome(effect.route)
                is VerificationQrUiEffect.ShowError -> { /* Show Snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Подтверждение профиля") },
                actions = {
                    IconButton(onClick = { viewModel.handleEvent(VerificationQrUiEvent.RefreshClicked) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Покажите этот код сотруднику клиники для подтверждения вашей личности",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else if (uiState.session != null) {
                    val bitmap = remember(uiState.session?.qrPayload) {
                        generateQrCode(uiState.session!!.qrPayload, 512)
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            if (uiState.session != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = uiState.session!!.shortCode,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Короткий код",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (uiState.isConfirmed) {
                Text(
                    text = "✓ Профиль подтвержден!",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.width(200.dp).height(4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ожидание подтверждения...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // T032 [DEBUG] Simulate Success
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = { 
                    // To simulate success, we can't easily trigger the flow from outside
                    // but we can make the VM navigate
                    onNavigateToHome("home")
                }
            ) {
                Text("[DEBUG] Simulate Success Transition")
            }
        }
    }
}

private fun generateQrCode(content: String, size: Int): Bitmap? {
    return try {
        val encoder = BarcodeEncoder()
        encoder.encodeBitmap(content, BarcodeFormat.QR_CODE, size, size)
    } catch (e: Exception) {
        null
    }
}
