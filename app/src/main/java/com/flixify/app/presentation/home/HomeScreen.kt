package com.flixify.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.flixify.app.domain.model.Movie
import com.flixify.app.presentation.common.theme.*
import com.flixify.app.presentation.navigation.Screen

@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    onMovieClick: (String) -> Unit,
    onSeriesClick: (String) -> Unit,
    onLiveTvClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Section
            item {
                if (uiState.heroContent != null) {
                    HeroSection(
                        movie = uiState.heroContent!!,
                        onPlayClick = { onMovieClick(uiState.heroContent!!.id) },
                        onMoreInfoClick = { onMovieClick(uiState.heroContent!!.id) }
                    )
                }
            }
            
            // Continue Watching
            if (uiState.continueWatching.isNotEmpty()) {
                item {
                    ContentRow(
                        title = "İzlemeye Devam Et",
                        movies = uiState.continueWatching,
                        onMovieClick = onMovieClick
                    )
                }
            }
            
            // Trending Movies
            if (uiState.trendingMovies.isNotEmpty()) {
                item {
                    ContentRow(
                        title = "Trend Filmler",
                        movies = uiState.trendingMovies,
                        onMovieClick = onMovieClick
                    )
                }
            }
            
            // New Releases
            if (uiState.newReleases.isNotEmpty()) {
                item {
                    ContentRow(
                        title = "Yeni Çıkanlar",
                        movies = uiState.newReleases,
                        onMovieClick = onMovieClick
                    )
                }
            }
            
            // Popular Series
            if (uiState.popularSeries.isNotEmpty()) {
                item {
                    SeriesRow(
                        title = "Popüler Diziler",
                        series = uiState.popularSeries,
                        onSeriesClick = onSeriesClick
                    )
                }
            }
            
            // Live TV Preview
            if (uiState.liveChannels.isNotEmpty()) {
                item {
                    LiveTvPreview(
                        channels = uiState.liveChannels.take(5),
                        onMoreClick = onLiveTvClick,
                        onChannelClick = { channelId ->
                            // Navigate to live player
                        }
                    )
                }
            }
        }
        
        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Accent)
            }
        }
    }
}

@Composable
private fun HeroSection(
    movie: Movie,
    onPlayClick: () -> Unit,
    onMoreInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
    ) {
        // Background image
        AsyncImage(
            model = movie.artwork?.backdropUrl ?: movie.artwork?.posterUrl,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Background.copy(alpha = 0.3f),
                            Background.copy(alpha = 0.7f),
                            Background
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Logo/Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Accent, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "F",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "FLIXIFY",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = movie.title,
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Metadata
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = movie.year?.toString() ?: "",
                    color = TextMuted,
                    fontSize = 14.sp
                )
                
                if (movie.duration != null) {
                    Text(
                        text = "•",
                        color = TextMuted
                    )
                    Text(
                        text = "${movie.duration} dk",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
                
                if (movie.rating != null) {
                    Text(
                        text = "•",
                        color = TextMuted
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${movie.rating}",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Overview
            Text(
                text = movie.overview ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Oynat")
                }
                
                OutlinedButton(
                    onClick = onMoreInfoClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Border)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Daha Fazla Bilgi")
                }
            }
        }
    }
}

@Composable
private fun ContentRow(
    title: String,
    movies: List<Movie>,
    onMovieClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant)
        ) {
            AsyncImage(
                model = movie.artwork?.posterUrl ?: movie.artwork?.backdropUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Rating badge
            if (movie.rating != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.7f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "%.1f".format(movie.rating),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = movie.year?.toString() ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
private fun SeriesRow(
    title: String,
    series: List<com.flixify.app.domain.model.Series>,
    onSeriesClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(series) { s ->
                SeriesCard(
                    series = s,
                    onClick = { onSeriesClick(s.id) }
                )
            }
        }
    }
}

@Composable
private fun SeriesCard(
    series: com.flixify.app.domain.model.Series,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant)
        ) {
            AsyncImage(
                model = series.artwork?.posterUrl ?: series.artwork?.backdropUrl,
                contentDescription = series.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = series.title,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = "${series.numberOfSeasons} Sezon",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
private fun LiveTvPreview(
    channels: List<com.flixify.app.domain.model.LiveChannel>,
    onMoreClick: () -> Unit,
    onChannelClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Canlı TV",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CANLI",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            TextButton(onClick = onMoreClick) {
                Text("Tümünü Gör", color = Accent)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(channels) { channel ->
                LiveChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel.id) }
                )
            }
        }
    }
}

@Composable
private fun LiveChannelCard(
    channel: com.flixify.app.domain.model.LiveChannel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant)
        ) {
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
            
            // Live indicator
            if (channel.isCurrentlyLive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = channel.name,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = channel.group ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}
