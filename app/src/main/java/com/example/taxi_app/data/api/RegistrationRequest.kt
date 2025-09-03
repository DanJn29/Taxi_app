//package com.example.taxi_app.data.api
//
//data class RegistrationRequest(
//    val name: String,
//    val email: String,
//    val password: String,
//    val password_confirmation: String
//)
//
//data class RegistrationResponse(
//    val message: String? = null,
//    val user: ApiUser? = null,
//    val token: String? = null,
//    val errors: Map<String, List<String>>? = null
//)
//
//data class ApiUser(
//    val id: Int,
//    val name: String,
//    val email: String,
//    val email_verified_at: String? = null,
//    val created_at: String,
//    val updated_at: String
//)
//
//data class ApiError(
//    val message: String,
//    val errors: Map<String, List<String>>? = null
//)

package com.example.taxi_app.data.api

data class RegistrationRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class RegistrationResponse(
    val user: UserResponse?,
    val message: String?
)

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?
)
