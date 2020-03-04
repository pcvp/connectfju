package com.connect.fju.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.connect.fju.R
import com.connect.fju.controllers.cardsController
import com.connect.fju.models.igrejaModel
import com.connect.fju.models.jovemModel
import com.github.kittinunf.fuel.core.FuelManager
import util.util
import kotlinx.android.synthetic.main.activity_cadastro.*

class CadastroActivity : AppCompatActivity() {

    var igrejas: List<igrejaModel>? = null
    var nomesIgrejas = ArrayList<String>()
    var idsIgrejas = ArrayList<Int>()
    var idIgreja: Int = 0
    var atalaia: String = ""
    var spinner: Spinner? = null

    var postValues = HashMap<String, Any>()

    override fun onResume() {
        super.onResume()
        cmbIgrejasResolve()
        cmbTribosResolve()
    }

    private fun goTo(activity: String) {
        try {
            if (activity == "login") {
                var intent = Intent(applicationContext, LoginActivity::class.java)
            } else if (activity == "esquecisenha") {
                var intent = Intent(applicationContext, EsqueciSenhaActivity::class.java)
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        } catch (e: java.lang.Exception) {
            util.toast(baseContext, e.message.toString())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)


        BtnGoToLogin.setOnClickListener {
            goTo("login")
        }

        BtnAtalaiaSim.setOnClickListener {
            atalaia = "S"
        }
        BtnAtalaiaNao.setOnClickListener {
            atalaia = "N"
        }

        btnCadastreSe.setOnClickListener {
            if (util.isConnected(baseContext) != true) {
                util.toast(baseContext, "Verifique sua conexão com a internet")
                return@setOnClickListener
            }

            postValues!!.put("nome", EdtNome.text.toString())
            postValues!!.put("email", EdtEmail.text.toString())
            postValues!!.put("celular", EdtCelular.text.toString())
            postValues!!.put("senha", EdtSenha.text.toString())
            postValues!!.put("confSenha", EdtConfSenha.text.toString())
            postValues!!.put("atalaia", atalaia)

            if (validaCampos(postValues)) {
                btnCadastreSe.isEnabled = false
                btnCadastreSe.setText("Aguarde");
                var fuel: FuelManager = FuelManager()
                fuel.post(util.URL_API + "addFirme", toListOf(postValues))
                    .responseString { request, response, result ->
                        try {
                            if (response.statusCode == 200) {
                                val (res, error) = result
                                if (res != null) {
                                    util.toast(baseContext!!, "Cadastro realizado com sucesso!!")
                                    goTo("login")
                                } else {
                                    throw Exception("Erro ao tentar fazer cadastro!")
                                }
                            } else if (response.statusCode == 400) {
                                throw Exception("Erro nos dados informados!")
                            } else {
                                throw Exception("Erro na requisição!")
                            }
                        } catch (e: Exception) {
                            util.toast(baseContext!!, e.message.toString())
                        } finally {
                            btnCadastreSe.isEnabled = true
                            btnCadastreSe.setText("Salvar");
                        }
                    }
            }
        }
    }

    private fun validaCampos(postValues: HashMap<String, Any>): Boolean {
        if (postValues.get("nome").toString().isEmpty()) {
            util.toast(baseContext!!, "Preencha o seu nome !!")
            return false
        }
        if (postValues.get("email").toString().isEmpty()) {
            util.toast(baseContext!!, "Preencha o seu email!!")
            return false
        }
        if (postValues.get("celular").toString().isEmpty() || postValues.get("celular").toString().length != 11) {
            util.toast(baseContext!!, "Preencha o número do seu celular!!")
            return false
        }

        if (postValues.get("senha").toString().isEmpty()) {
            util.toast(baseContext!!, "Preencha a senha !!")
            return false
        }

        if (postValues.get("senha").toString() != postValues.get("confSenha").toString()) {
            util.toast(baseContext!!, "A senhas informadas são diferentes !!")
            return false
        }

        if (postValues.get("id_igreja").toString() == "" || !postValues.contains("id_igreja")) {
            util.toast(baseContext!!, "Selecione sua igreja !!")
            return false
        }

        if (postValues.get("tribo").toString() == "" || !postValues.contains("tribo")) {
            util.toast(baseContext!!, "Selecione sua tribo !!")
            return false
        }

        if (postValues.get("atalaia").toString() == "") {
            util.toast(baseContext!!, "Selecione se você é ou não atalaia !!")
            return false
        }

        return true
    }


    private fun toListOf(postValues: HashMap<String, Any>): List<Pair<String, Any?>>? {


        return listOf(
            "nome" to postValues.get("nome"),
            "email" to postValues.get("email"),
            "celular" to postValues.get("celular"),
            "senha" to postValues.get("senha"),
            "atalaia" to postValues.get("atalaia"),
            "tribo" to postValues.get("tribo"),
            "id_igreja" to postValues.get("id_igreja")
        )

    }

    private fun cmbTribosResolve() {
        try {
            cmbTribo.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (!cmbTribo.getSelectedItem().equals("Selecione sua tribo"))
                            postValues!!.set("tribo", cmbTribo.getSelectedItem().toString())
                    }

                }


        } catch (e: Exception) {
            util.toast(baseContext, e.message.toString())
        }
    }

    private fun cmbIgrejasResolve() {

        try {
            var fuel: FuelManager = FuelManager()
            fuel.get(util.URL_API + "listarIgrejas/")
                .responseObject(igrejaModel.Deserializer()) { request, response, result ->
                    val (igreja, err) = result


                    nomesIgrejas.add("Selecione sua igreja")
                    idsIgrejas.add(0)

                    igreja?.forEach { igreja ->
                        nomesIgrejas.add(igreja.nome)
                        idsIgrejas.add(igreja.id)
                    }


                    progressBar.visibility = View.GONE


                    val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomesIgrejas)

                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    cmbIgrejas!!.adapter = aa

                    cmbIgrejas.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                try {
                                    if (position != 0)
                                        postValues!!.set("id_igreja", idsIgrejas[position])
                                } catch (e: Exception) {
                                    util.toast(baseContext, e.message.toString())
                                }
                            }

                        }

                }
        } catch (e: Exception) {
            util.toast(baseContext, e.message.toString())
        }


    }
}

