package com.flixify.app.data.remote

import com.flixify.app.domain.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.*

interface FlixifyApi {
    
    // Auth
    @POST("auth/register-anon")
    suspend fun registerAnon(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login-by-code")
    suspend fun loginByCode(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<AuthResponse>
    
    // User
    @GET("me")
    suspend fun getMe(): Response<User>
    
    // Settings
    @GET("settings/public")
    suspend fun getPublicSettings(): Response<PublicSettings>
    
    // Packages
    @GET("admin/packages/public")
    suspend fun getPackages(): Response<PackageResponse>
    
    // Payment Methods
    @GET("payment-methods/public")
    suspend fun getPaymentMethods(): Response<PaymentMethodResponse>
    
    @GET("me/payment-requests")
    suspend fun getPaymentRequests(): Response<PaymentRequestResponse>
    
    @POST("me/payment-requests")
    suspend fun createPaymentRequest(@Body request: CreatePaymentRequest): Response<Unit>
    
    // Catalog - Live TV
    @GET("me/catalog/live")
    suspend fun getLiveCatalog(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 300,
        @Query("search") search: String? = null,
        @Query("group") group: String? = null
    ): Response<LiveCatalogResponse>
    
    // Catalog - Movies
    @GET("me/catalog/movies")
    suspend fun getMovieCatalog(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 120,
        @Query("search") search: String? = null,
        @Query("group") group: String? = null
    ): Response<MovieCatalogResponse>
    
    // Catalog - Series
    @GET("me/catalog/series")
    suspend fun getSeriesCatalog(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 200,
        @Query("search") search: String? = null
    ): Response<SeriesCatalogResponse>
    
    // Playback - Live
    @GET("me/native/live/{channelId}/playback")
    suspend fun getLivePlaybackUrl(
        @Path("channelId") channelId: String,
        @Query("platform") platform: String = "android-native",
        @Query("clientRuntime") clientRuntime: String = "native",
        @Query("preferRelay") preferRelay: Int = 1,
        @Query("cacheProfile") cacheProfile: String = "fast",
        @Query("forceRelayRestart") forceRelayRestart: Int? = null
    ): Response<PlaybackSource>
    
    // Playback - VOD
    @GET("me/native/vod/{kind}/{itemId}/playback")
    suspend fun getVodPlaybackUrl(
        @Path("kind") kind: String,
        @Path("itemId") itemId: String,
        @Query("platform") platform: String = "android-native",
        @Query("audioTrackId") audioTrackId: String? = null
    ): Response<VodPlaybackResponse>
    
    // Health Check
    @POST("me/live/{channelId}/health")
    suspend fun reportLiveHealth(
        @Path("channelId") channelId: String,
        @Body event: PlaybackEvent
    ): Response<Unit>
    
    @POST("me/vod/{kind}/{itemId}/health")
    suspend fun reportVodHealth(
        @Path("kind") kind: String,
        @Path("itemId") itemId: String,
        @Body event: PlaybackEvent
    ): Response<Unit>
    
    // Trial Request
    @POST("me/trial-request")
    suspend fun requestTrial(@Body request: TrialRequest): Response<Unit>
}

@Serializable
data class PublicSettings(
    @SerialName("supportWhatsappUrl") val supportWhatsappUrl: String? = null,
    @SerialName("supportTelegramUrl") val supportTelegramUrl: String? = null
)

@Serializable
data class PackageResponse(
    val items: List<PaymentPackage>
)

@Serializable
data class PaymentMethodResponse(
    val items: List<PaymentMethod>
)

@Serializable
data class PaymentRequestResponse(
    val items: List<PaymentRequest>
)

@Serializable
data class PlaybackEvent(
    val event: String,
    @SerialName("clientRuntime") val clientRuntime: String = "native",
    @SerialName("playerEngine") val playerEngine: String = "exoplayer",
    @SerialName("decoderMode") val decoderMode: String? = null,
    @SerialName("sourceTransport") val sourceTransport: String? = null,
    @SerialName("deliveryMode") val deliveryMode: String? = null,
    @SerialName("audioTrackId") val audioTrackId: String? = null,
    @SerialName("currentTime") val currentTime: Double? = null,
    @SerialName("errorCode") val errorCode: String? = null,
    @SerialName("errorMessage") val errorMessage: String? = null,
    val detail: Map<String, JsonElement>? = null
)

@Serializable
data class TrialRequest(
    val note: String? = null
)
