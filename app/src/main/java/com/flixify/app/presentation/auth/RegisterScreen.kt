package com.flixify.app.presentation.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flixify.app.presentation.common.theme.*
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background gradient circles
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-150).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Success.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(200.dp)
                )
        )
        
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 250.dp, y = 50.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(150.dp)
                )
        )
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.generatedCode.isEmpty()) {
                        // Initial state - Generate code button
                        GenerateCodeContent(
                            isGenerating = state.isGenerating,
                            onGenerate = { viewModel.generateCode() }
                        )
                    } else {
                        // Code revealed state
                        CodeRevealContent(
                            state = state,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(state.generatedCode))
                            },
                            onAcknowledgeToggle = { viewModel.toggleAcknowledged() },
                            onContinue = { onNavigateToLogin() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Back to login link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Zaten hesabınız var mı? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        Text(
                            text = "Giriş Yap",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = AccentStrong,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                    
                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateCodeContent(
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    // Title
    Text(
        text = "Hoş Geldiniz",
        style = MaterialTheme.typography.displaySmall,
        color = TextPrimary
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Description
    val descriptionText = buildAnnotatedString {
        append("Lütfen ")
        withStyle(style = SpanStyle(color = Accent)) {
            append("özel erişim kodunuzu")
        }
        append(" oluşturun ve gelecekteki erişim için kaydedin.")
    }
    
    Text(
        text = descriptionText,
        style = MaterialTheme.typography.bodyMedium,
        color = TextMuted,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Generate button
    if (isGenerating) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(ButtonShape)
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Accent,
                strokeWidth = 3.dp
            )
        }
    } else {
        PrimaryButton(
            text = "Kod Oluştur",
            onClick = onGenerate,
            glow = true
        )
    }
}

@Composable
private fun CodeRevealContent(
    state: RegisterUiState,
    onCopy: () -> Unit,
    onAcknowledgeToggle: () -> Unit,
    onContinue: () -> Unit
) {
    val scrambledDisplay = buildString {
        val code = state.scrambledCode.chunked(4).joinToString(" ")
        append(code)
    }
    
    // Title
    Text(
        text = "Kod Oluşturuldu",
        style = MaterialTheme.typography.displaySmall,
        color = TextPrimary
    )
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Description
    Text(
        text = "Aşağıdaki özel erişim kodunuzu kaydedin:",
        style = MaterialTheme.typography.bodyMedium,
        color = TextMuted,
        textAlign = TextAlign.Center
    )
    
    Spacer(modifier = Modifier.height(20.dp))
    
    // Code display card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(GlassCardShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E2430),
                        Color(0xFF181C26)
                    )
                )
            )
            .border(1.dp, Accent.copy(alpha = 0.3f), GlassCardShape)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scrambled code display
            Text(
                text = scrambledDisplay,
                style = MaterialTheme.typography.displayMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = if (state.isRevealing) Accent else Color.White,
                textAlign = TextAlign.Center
            )
            
            if (state.isRevealing) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Kod açığa çıkıyor...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Progress bar
    if (state.isRevealing) {
        LinearProgressIndicator(
            progress = state.progress,
            modifier = Modifier.fillMaxWidth(),
            color = Accent,
            trackColor = SurfaceVariant
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Copy button (only when fully revealed)
    if (!state.isRevealing) {
        PrimaryButton(
            text = "Kodu Kopyala",
            onClick = onCopy,
            secondary = true
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Warning card
    WarningCard()
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Acknowledge checkbox
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAcknowledgeToggle() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = state.isAcknowledged,
            onCheckedChange = { onAcknowledgeToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Accent,
                uncheckedColor = TextMuted
            )
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "Erişim kodumu güvenli bir şekilde kaydettiğimi onaylıyorum",
            style = MaterialTheme.typography.bodyMedium,
            color = if (state.isAcknowledged) TextPrimary else TextMuted
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // Continue button
    PrimaryButton(
        text = "Devam Et",
        onClick = onContinue,
        enabled = state.isAcknowledged && !state.isRevealing,
        glow = true
    )
}

@Composable
private fun WarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF261818))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "⚠️ Önemli Uyarı",
                style = MaterialTheme.typography.titleSmall,
                color = Error,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Bu kod gelecekte hesabınıza erişim için gereklidir ve geri alınamaz. Lütfen şimdi kopyalayıp güvenli bir yerde saklayın.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
