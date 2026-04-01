package com.flixify.app.presentation.player

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {
    
    private val viewModel: PlayerViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private var adsLoader: ImaAdsLoader? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Extract extras
        val contentId = intent.getStringExtra(EXTRA_CONTENT_ID) ?: ""
        val contentType = intent.getStringExtra(EXTRA_CONTENT_TYPE) ?: "movie"
        val title = intent.getStringExtra(EXTRA_TITLE) ?: ""
        val isLive = intent.getBooleanExtra(EXTRA_IS_LIVE, false)
        
        // Enable immersive mode
        enableImmersiveMode()
        
        setContent {
            PlayerScreen(
                contentId = contentId,
                contentType = contentType,
                title = title,
                isLive = isLive,
                viewModel = viewModel,
                onBackPressed = { finish() },
                onToggleFullscreen = { toggleFullscreen() }
            )
        }
        
        // Load content
        viewModel.loadContent(contentId, contentType, isLive)
    }
    
    private fun enableImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = 
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    private fun toggleFullscreen() {
        requestedOrientation = if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
    
    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
        adsLoader?.release()
        adsLoader = null
    }
    
    companion object {
        const val EXTRA_CONTENT_ID = "content_id"
        const val EXTRA_CONTENT_TYPE = "content_type"
        const val EXTRA_TITLE = "title"
        const val EXTRA_IS_LIVE = "is_live"
        
        fun launch(
            context: Context,
            contentId: String,
            contentType: String,
            title: String,
            isLive: Boolean = false
        ) {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_CONTENT_ID, contentId)
                putExtra(EXTRA_CONTENT_TYPE, contentType)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_IS_LIVE, isLive)
            }
            context.startActivity(intent)
        }
    }
}

@Composable
fun PlayerScreen(
    contentId: String,
    contentType: String,
    title: String,
    isLive: Boolean,
    viewModel: PlayerViewModel,
    onBackPressed: () -> Unit,
    onToggleFullscreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Create ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setLiveTargetOffsetMs(5000)
            )
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
            .apply {
                playWhenReady = true
            }
    }
    
    // Update player when URL changes
    LaunchedEffect(uiState.playbackUrl) {
        uiState.playbackUrl?.let { url ->
            val mediaItem = MediaItem.Builder()
                .setUri(url)
                .build()
            
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }
    
    // Track player state
    LaunchedEffect(exoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> viewModel.onPlayerReady()
                    Player.STATE_BUFFERING -> viewModel.onBuffering()
                    Player.STATE_ENDED -> viewModel.onPlaybackEnded()
                    Player.STATE_IDLE -> {}
                }
            }
            
            override fun onPlayerError(error: PlaybackException) {
                viewModel.onError(error.message ?: "Playback error")
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                viewModel.onIsPlayingChanged(isPlaying)
            }
        })
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                StyledPlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setKeepContentOnPlayerReset(true)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Custom UI Overlay
        VideoOverlay(
            title = title,
            isLive = isLive,
            isPlaying = uiState.isPlaying,
            isBuffering = uiState.isBuffering,
            currentTime = exoPlayer.currentPosition,
            duration = exoPlayer.duration.takeIf { it > 0 } ?: 0L,
            onBackPressed = onBackPressed,
            onToggleFullscreen = onToggleFullscreen,
            onPlayPause = {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
            },
            onSeek = { positionMs ->
                exoPlayer.seekTo(positionMs)
            },
            onSeekBack = {
                exoPlayer.seekBack()
            },
            onSeekForward = {
                exoPlayer.seekForward()
            }
        )
    }
}

@Composable
private fun VideoOverlay(
    title: String,
    isLive: Boolean,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentTime: Long,
    duration: Long,
    onBackPressed: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var wasPlayingBeforeSeek by remember { mutableStateOf(true) }
    
    // Auto hide controls
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3000)
            showControls = false
        }
    }
    
    // Tap to toggle controls
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { showControls = !showControls },
                    onDoubleTap = { offset ->
                        val width = size.width
                        if (offset.x < width / 2) {
                            onSeekBack()
                        } else {
                            onSeekForward()
                        }
                    }
                )
            }
    ) {
        // Buffering indicator
        if (isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }
        }
        
        // Top bar (title and back)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.7f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (isLive) {
                            Text(
                                text = "● CANLI",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    IconButton(onClick = onToggleFullscreen) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        
        // Center play button (when paused)
        if (!isPlaying && !isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
        
        // Bottom controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    // Seek bar (hide for live)
                    if (!isLive && duration > 0) {
                        Slider(
                            value = currentTime.toFloat().coerceIn(0f, duration.toFloat()),
                            onValueChange = { newValue ->
                                if (wasPlayingBeforeSeek) {
                                    wasPlayingBeforeSeek = isPlaying
                                }
                                onSeek(newValue.toLong())
                            },
                            onValueChangeFinished = {
                                if (wasPlayingBeforeSeek) {
                                    onPlayPause() // Resume playing
                                }
                            },
                            valueRange = 0f..duration.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color(0xFFE50914),
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        // Time display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatDuration(currentTime),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = formatDuration(duration),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Seek back
                        IconButton(onClick = onSeekBack) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Back 10s",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        // Play/Pause
                        IconButton(
                            onClick = onPlayPause,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFE50914), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        // Seek forward
                        IconButton(onClick = onSeekForward) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
