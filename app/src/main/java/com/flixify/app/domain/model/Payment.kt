package com.flixify.app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentPackage(
    val id: String,
    val slug: String,
    val title: String,
    @SerialName("durationMonths") val durationMonths: Int,
    @SerialName("priceLabel") val priceLabel: String? = null
)

@Serializable
data class PaymentMethod(
    val id: String,
    val label: String,
    val enabled: Boolean,
    val details: String? = null,
    @SerialName("bankTransfer") val bankTransfer: BankTransferInfo? = null,
    @SerialName("cryptoAssets") val cryptoAssets: List<CryptoAsset>? = null
)

@Serializable
data class BankTransferInfo(
    @SerialName("recipientName") val recipientName: String? = null,
    val iban: String? = null,
    @SerialName("bankName") val bankName: String? = null
)

@Serializable
data class CryptoAsset(
    val id: String,
    val symbol: String,
    val label: String,
    @SerialName("walletAddress") val walletAddress: String? = null
)

@Serializable
data class PaymentRequest(
    val id: String,
    @SerialName("packageTitle") val packageTitle: String,
    val status: String,
    @SerialName("createdAt") val createdAt: String
)

@Serializable
data class CreatePaymentRequest(
    @SerialName("packageSlug") val packageSlug: String,
    @SerialName("paymentMethodId") val paymentMethodId: String,
    @SerialName("cryptoAssetId") val cryptoAssetId: String? = null
)
