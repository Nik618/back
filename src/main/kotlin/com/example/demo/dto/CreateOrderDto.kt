package com.example.demo.dto

data class CreateOrderDto (
        val description: String? = null,
        var photo: ByteArray? = null,
        var file: ByteArray? = null,
        var extension: String? = null,
        var mimeType: String? = null,
        val address: String? = null,
        var user: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreateOrderDto

        if (description != other.description) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        if (file != null) {
            if (other.file == null) return false
            if (!file.contentEquals(other.file)) return false
        } else if (other.file != null) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description?.hashCode() ?: 0
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        result = 31 * result + (file?.contentHashCode() ?: 0)
        result = 31 * result + (user?.hashCode() ?: 0)
        return result
    }
}