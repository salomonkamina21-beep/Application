package com.precensia.app.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "" // "PROFESSOR", "DELEGATE", "STUDENT"
)
