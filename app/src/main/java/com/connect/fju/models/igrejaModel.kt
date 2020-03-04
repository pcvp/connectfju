package com.connect.fju.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class igrejaModel(
    val id: Int = 0,
    val nome: String = "",
    val cards: List<cardsModel>
) {
    class Deserializer : ResponseDeserializable<Array<igrejaModel>> {
        override fun deserialize(content: String): Array<igrejaModel>? =
            Gson().fromJson(content, Array<igrejaModel>::class.java)
    }
}