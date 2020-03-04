package com.connect.fju.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class jovemModel(
    val id: Int = 0,
    val nome: String = "",
    val sobrenome: String = "",
    val nascimento: String = "",
    val celular: String = "",
    val bairro: String = "",
    val referencia: String = "",
    val opcoes: String = "",
    val tribo: String = "",
    val dataEVG: String = "",
    val localEVG: String,
    val id_firme: Int? = null,
    val id_igreja: Int? = null
) {
    class Deserializer : ResponseDeserializable<Array<jovemModel>> {
        override fun deserialize(content: String): Array<jovemModel>? =
            Gson().fromJson(content, Array<jovemModel>::class.java)
    }
}


