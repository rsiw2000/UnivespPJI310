package com.univesp.pji310.euindico.data.remote

import com.univesp.pji310.euindico.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<GenericResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<GenericResponse>

    // User Profile
    @GET("user/profile")
    suspend fun getUserProfile(@Header("X-Username") username: String): Response<UserResponse>

    @PUT("user/profile")
    suspend fun updateUserProfile(
        @Header("X-Username") username: String,
        @Body request: UpdateProfileRequest
    ): Response<UserProfile>

    // User Professions
    @GET("user/professions")
    suspend fun getUserProfessions(@Header("X-Username") username: String): Response<DataWrapper<MyProfessionResult>>

    @POST("user/professions")
    suspend fun addUserProfession(
        @Header("X-Username") username: String,
        @Body request: AddProfessionRequest
    ): Response<GenericResponse>

    // Locations
    @GET("locations/states")
    suspend fun getStates(): Response<DataWrapper<StateResponse>>

    @GET("locations/cities/{stateCode}")
    suspend fun getCities(@Path("stateCode") stateCode: String): Response<DataWrapper<CityResponse>>

    // Professions Definitions
    @GET("professions")
    suspend fun getProfessions(): Response<DataWrapper<Profession>>

    @POST("professions")
    suspend fun createProfession(@Body profession: Profession): Response<Profession>

    // Search Professionals
    @GET("search/professionals")
    suspend fun searchProfessionals(
        @Header("X-Username") username: String,
        @Query("idProfissao") idProfissao: Int
    ): Response<DataWrapper<ProfessionalResult>>

    // Reviews
    @POST("reviews")
    suspend fun createReview(
        @Header("X-Username") username: String,
        @Body review: ReviewRequest
    ): Response<GenericResponse>
}
