package com.flixify.app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flixify.app.presentation.common.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background gradient circles (like Windows version)
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-100).dp, y = (-150).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.13f),
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
                            Info.copy(alpha = 0.13f),
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
                    // Logo
                    LogoHeader()
                    
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    // Title
                    Text(
                        text = "Güvenli Erişim",
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary
                    )
                    
                    Text(
                        text = "16 haneli özel erişim kodunuzu girin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Code Input
                    Text(
                        text = "Erişim Kodu",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Custom code input field
                    CodeInputField(
                        value = state.formattedCode,
                        onValueChange = { newValue ->
                            // Remove spaces and pass raw
                            viewModel.onCodeChanged(newValue.replace(" ", ""))
                        },
                        showCode = state.showCode,
                        onToggleShow = { viewModel.toggleShowCode() },
                        isLoading = state.isLoading
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Progress segments
                    CodeProgressIndicator(progress = state.progress)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Character count
                    Text(
                        text = "${state.code.length}/16",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                    
                    // Error message
                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Login button
                    PrimaryButton(
                        text = if (state.isLoading) "Giriş Yapılıyor..." else "OTURUM AÇ",
                        onClick = { viewModel.login() },
                        enabled = state.isValid && !state.isLoading,
                        glow = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Register link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hesabınız yok mu? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        Text(
                            text = "Hesap Oluştur",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = AccentStrong,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
    
    // Auto navigate on success
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading && state.error == null && state.code.isNotEmpty()) {
            // Check if actually logged in via auth state
        }
    }
}

@Composable
private fun LogoHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Logo icon placeholder
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Accent, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "F",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = "FLIXIFY",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(28.dp)
                .background(Accent, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PRO",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CodeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    showCode: Boolean,
    onToggleShow: () -> Unit,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Main input
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = !isLoading,
            singleLine = true,
            visualTransformation = if (showCode) VisualTransformation.None else PasswordVisualTransformation('•'),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            placeholder = { Text("X7F2 A9B1 C4D8 E6F0", color = TextMuted) },
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                letterSpacing = 1.8.sp,
                color = Color.White
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant,
                disabledContainerColor = SurfaceVariant,
                focusedBorderColor = Accent,
                unfocusedBorderColor = Border,
                disabledBorderColor = Border
            ),
            shape = InputShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
        )
        
        // Show/Hide button
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 11.dp)
                .width(82.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (showCode) 
                            listOf(Color(0xFF272B35), Color(0xFF1E212A))
                        else 
                            listOf(Color(0xFF222733), Color(0xFF1A1D25))
                    )
                )
                .clickable { onToggleShow() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (showCode) "Gizle" else "Göster",
                color = Color(0xFFEEF3FB),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CodeProgressIndicator(progress: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filledSegments = (progress * 4).toInt()
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index < filledSegments) Accent else BorderSoft.copy(alpha = 0.1f)
                    )
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(GlassCardShape)
            .background(Panel)
            .border(1.dp, BorderSoft, GlassCardShape)
    ) {
        content()
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    glow: Boolean = false,
    secondary: Boolean = false
) {
    val scale = remember { mutableStateOf(1f) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale.value)
            .clip(ButtonShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (secondary) 
                        listOf(ButtonSecondary, ButtonSecondaryPressed)
                    else 
                        listOf(ButtonPrimary, ButtonPrimaryPressed)
                )
            )
            .border(
                1.dp,
                if (secondary) Border else Accent,
                ButtonShape
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (secondary) TextPrimary else Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Extension for border
defun Modifier.border(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape) = this.then(
    androidx.compose.foundation.border(width, color, shape)
)
