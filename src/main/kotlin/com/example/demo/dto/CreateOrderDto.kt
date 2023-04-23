package com.example.demo.dto

import com.example.demo.dto.enums.RoleEnum

data class CreateOrderDto (
        val description: String? = null,
        var photo: ByteArray? = null
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

        return true
    }

    override fun hashCode(): Int {
        var result = description?.hashCode() ?: 0
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        return result
    }
}