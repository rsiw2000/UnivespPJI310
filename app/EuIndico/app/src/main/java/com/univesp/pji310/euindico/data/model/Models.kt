package com.univesp.pji310.euindico.data.model

import com.google.gson.annotations.SerializedName

// 1. Auth Endpoint Models
data class LoginRequest(
    val username: String,
    val password: String? = null // If they enter password
)

data class GenericResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val token: String? = null
)

// Data Wrapper for responses returning a list
data class DataWrapper<T>(
    val success: Boolean,
    val data: List<T>
)

// 2. Profile Endpoint Models
data class UserProfile(
    val id: Int? = null,
    val username: String? = null,
    val nome: String? = null,
    val telefone: String? = null,
    val estado: String? = null,
    val cidade: Any? = null, // Can be Int or String based on different endpoints
    val bairro: String? = null,
    val cpfCnpj: Long? = null,
    val email: String? = null
)

data class UserResponse(
    val success: Boolean,
    val user: UserProfile
)

data class UpdateProfileRequest(
    val nome: String,
    val telefone: String,
    val estado: String,
    val cidade: Int,
    val bairro: String
)

// 3. Professions and
data class ReviewRequest(
    val idProfissao: Int,
    val idPrestador: Int,
    val avaliacao: Int,
    val comentario: String
)
data class Profession(
    val id: Int,
    val nome: String
)

data class MyProfessionResult(
    val id: Int,
    val nome: String,
    val situacao: String? = null,
    val avaliacao: String? = null
)

data class AddProfessionRequest(
    val id: Int
)

// 4. Search Professional Models
data class ProfessionalResult(
    @SerializedName("id") val idPrestador: Int,
    val idProfissao: Int,
    val nome: String? = null,
    val avaliacao: String? = null,
    val cidade: String? = null,
    val estado: String? = null
)

// 5. Locations Models
data class StateResponse(
    val uf: String,
    val nome: String? = null
)

data class CityResponse(
    val ibge: Int? = null,
    val id: Int,
    val nome: String,
    val uf: String? = null
)

// 6. Review Request
data class AddReviewRequest(
    val idProfissao: Int,
    val idPrestador: Int,
    val avaliacao: Int,
    val comentario: String
)
