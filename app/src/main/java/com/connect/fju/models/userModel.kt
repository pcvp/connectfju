package com.connect.fju.models

data class userModel(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val celular: String = "",
    val tribo: String = "",
    val lider: String = "",
    val atalaia: String = "",
    val senha: String? = null,
    val igreja: igrejaModel? = null,
    var token: String? = null
) {
}