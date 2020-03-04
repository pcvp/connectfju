package com.connect.fju.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.connect.fju.R
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_esqueci_senha.*
import util.util

class EsqueciSenhaActivity : AppCompatActivity() {

    var postValues = HashMap<String, Any>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_senha)


        btnRecuperarSenha.setOnClickListener {
            postValues!!.put("celular", EdtCelular.text.toString())
            postValues!!.put("email", EdtEmail.text.toString())
            if (validaCampos(postValues)) {
                btnRecuperarSenha.isEnabled = false
                btnRecuperarSenha.setText("Aguarde");
                var fuel: FuelManager = FuelManager()
                fuel.post(util.URL_API + "recuperarSenha", toListOf(postValues))
                    .responseString { request, response, result ->
                        try {
                            if (response.statusCode == 200) {
                                val (res, error) = result
                                if (res != null) {
                                    util.toast(
                                        baseContext!!,
                                        "Uma nova senha foi enviada para o seu email"
                                    )
                                } else {
                                    throw Exception("Erro ao tentar recuperar sua senha!")
                                }
                            } else if (response.statusCode == 400) {
                                throw Exception("Erro nos dados informados!")
                            } else {
                                throw Exception("Erro na requisição!")
                            }
                        } catch (e: Exception) {
                            util.toast(baseContext!!, e.message.toString())
                        } finally {
                            btnRecuperarSenha.isEnabled = true
                            btnRecuperarSenha.setText("Recuperar senha");
                        }
                    }
            }
        }
    }

    private fun validaCampos(postValues: HashMap<String, Any>): Boolean {
        if (postValues.get("email").toString().isEmpty()) {
            util.toast(baseContext!!, "Preencha o seu email!!")
            return false
        }
        if (postValues.get("celular").toString().isEmpty() || postValues.get("celular").toString().length != 11) {
            util.toast(baseContext!!, "Preencha o número do seu celular!!")
            return false
        }
        return true
    }


    private fun toListOf(postValues: HashMap<String, Any>): List<Pair<String, Any?>>? {


        return listOf(
            "email" to postValues.get("email"),
            "celular" to postValues.get("celular")
        )

    }
}
