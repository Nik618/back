package com.example.demo.dto

import java.security.cert.Extension

data class OrderDto(
        val id: Int? = null,
        val description: String? = null,
        val photo: ByteArray? = null,
        val file: ByteArray? = null,
        var extension: String? = null,
        var mimeType: String? = null,
        val status: String? = null,
        val price: String? = null,
        val track: String? = null,
        val address: String? = null,
        val paymentAddress: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderDto

        if (id != other.id) return false
        if (description != other.description) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        if (status != other.status) return false
        if (price != other.price) return false
        if (track != other.track) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (track?.hashCode() ?: 0)
        return result
    }
}