package com.connect.fju.controllers

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import com.connect.fju.activity.MainActivity
import com.connect.fju.models.jovemModel
import com.github.kittinunf.fuel.core.FuelManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import com.connect.fju.models.userModel
import util.util


class userController(paramterActivity: Activity, paramterContext: Context) {


    companion object {
        private lateinit var activity: Activity
        private lateinit var context: Context

        fun userLogin() {
            var intent = Intent(context.applicationContext, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }


    }

    init {
        activity = paramterActivity
        context = paramterContext
    }

    fun login(user: userModel) {

        val pd = ProgressDialog(activity)
        pd.setMessage("Autenticando")
        pd.show()
        activity.btnLogin.isEnabled = false


        var prefs = userController.context!!.getSharedPreferences("token", Context.MODE_PRIVATE);
        if (prefs.contains("token"))
            user.token = prefs.getString("token", null)

        var fuel: FuelManager = FuelManager()

        fuel.post(util.URL_API + "login", toListOf(user))
            .responseString { _, response, result ->
                try {
                    if (response.statusCode == 200) {
                        val (res, _) = result
                        if (res != null) {
                            val user: userModel = Gson().fromJson(
                                res,
                                userModel::class.java
                            )

                            util.saveMessage(
                                context,
                                "welcomeWPP",
                                "Olá *nomedojovem*! Aqui é *meunome*, foi muito bacana falar contigo e também saber o que vc curte!! E como eu te disse no próximo fds vamos nos reunir no Arena FJU conto com a sua presença lá hein !! \n" +
                                        "Aaa e antes que eu esqueça esse é o insta da FJU https://instagr.am/oficialfju segue lá pra ficar por dentro das novidades \uD83D\uDE09"
                            )
                            util.saveMessage(
                                context,
                                "welcomeSMS",
                                "Olá *nomedojovem*! Aqui é *meunome*, foi muito bacana falar contigo e também saber o que vc curte!! E como eu te disse no próximo fds vamos nos reunir no Arena FJU conto com a sua presença lá hein !! \n" +
                                        "Aaa e antes que eu esqueça esse é o insta da FJU https://instagr.am/oficialfju segue lá pra ficar por dentro das novidades \uD83D\uDE09"
                            )

                            save(user)
                            userLogin();
                        } else {
                            throw Exception("Erro ao tentar fazer login!")
                        }
                    } else if (response.statusCode == 400) {
                        throw Exception("Erro nos dados informados!")
                    } else if (response.statusCode == 401) {
                        throw Exception("Celular e/ou senhas inválidos!")
                    } else {
                        throw Exception("Erro na requisição!")
                    }
                } catch (e: Exception) {
                    util.toast(context, e.message.toString())
                    pd.hide()
                    activity.btnLogin.isEnabled = true
                }
            }

    }


    fun save(user: userModel): Boolean {

        try {

            var prefs = context.getSharedPreferences("usuario", MODE_PRIVATE)
            var editor = prefs!!.edit()
            editor.putString("usuario", Gson().toJson(user))
            editor.apply()
        } catch (e: Exception) {
            util.toast(
                context,
                "Será necessario fazer login novamente na próxima vez que usar o Connect."
            )
            return false
        } finally {
            return true
        }
    }

    private fun toListOf(user: userModel): List<Pair<String, Any?>>? {
        return listOf(
            "id" to user.id,
            "nome" to user.nome,
            "celular" to user.celular,
            "tribo" to user.tribo,
            "lider" to user.lider,
            "atalaia" to user.atalaia,
            "senha" to user.senha.toString(),
            "token" to user.token.toString()
        )
    }


    private fun toHashMap(user: userModel): HashMap<String, Any> {
        val map = HashMap<String, Any>()

        map["id"] = user.id
        map["nome"] = user.nome
        map["celular"] = user.celular
        map["tribo"] = user.tribo
        map["lider"] = user.lider
        map["atalaia"] = user.atalaia
        map["senha"] = user.senha.toString()

        return map
    }

    private fun toContentValues(user: userModel): ContentValues {
        val values = ContentValues()
        values.put("id", user.id)
        values.put("nome", user.nome)
        values.put("celular", user.celular)
        values.put("tribo", user.tribo)
        values.put("lider", user.lider)
        values.put("atalaia", user.atalaia)
        return values
    }

    fun getUser(): userModel? {
        var prefs = context.getSharedPreferences("usuario", MODE_PRIVATE);
        if (prefs.contains("usuario"))
            return Gson().fromJson(
                prefs.getString("usuario", null),
                userModel::class.java
            )
        else
            return null
    }

}

