package com.connect.fju.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.connect.fju.R
import com.connect.fju.controllers.userController
import com.connect.fju.models.igrejaModel
import com.connect.fju.models.userModel
import com.github.kittinunf.fuel.core.FuelManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cadastro.*
import kotlinx.android.synthetic.main.activity_cadastro.view.*
import kotlinx.android.synthetic.main.activity_cadastro.view.BtnAtalaiaNao
import kotlinx.android.synthetic.main.activity_cadastro.view.BtnAtalaiaSim
import kotlinx.android.synthetic.main.activity_cadastro.view.EdtCelular
import kotlinx.android.synthetic.main.activity_cadastro.view.EdtEmail
import kotlinx.android.synthetic.main.activity_cadastro.view.EdtNome
import kotlinx.android.synthetic.main.activity_cadastro.view.cmbIgrejas
import kotlinx.android.synthetic.main.activity_cadastro.view.cmbTribo
import kotlinx.android.synthetic.main.activity_cadastro.view.progressBar
import kotlinx.android.synthetic.main.fragment_profile.view.*
import util.util

/**
 * A simple [Fragment] subclass.
 */
class PerfilFragment : Fragment() {
    lateinit var user: userModel
    var postValues = HashMap<String, Any>()
    var nomesIgrejas = ArrayList<String>()
    var idsIgrejas = ArrayList<Int>()
    var idIgreja: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = userController(activity!!, context!!).getUser()!!

        var container = inflater.inflate(R.layout.fragment_profile, container, false)

        container.EdtNome.setText(user!!.nome)
        container.EdtEmail.setText(user!!.email)
        container.EdtCelular.setText(user!!.celular)
        postValues!!.put("id", user!!.id)
        postValues!!.put("atalaia", user!!.atalaia)
        postValues!!.put("lider", user!!.lider)

        if (user!!.atalaia == "S") {
            container.BtnAtalaiaSim.isChecked = true
        } else {
            container.BtnAtalaiaNao.isChecked = true
        }

        container.BtnAtalaiaSim.setOnClickListener {
            postValues!!.set("atalaia", "S")
        }
        container.BtnAtalaiaNao.setOnClickListener {
            postValues!!.set("atalaia", "N")
        }

        resolveCmbTribo(container)
        resolveCmbIgreja(container)


        val adapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.TribosProfile,
            android.R.layout.simple_spinner_item
        )
        container.cmbTribo.setSelection(adapter.getPosition(user.tribo))


        /* ----- RESOLVE IGREJAS ----- */




        container.BtnSalvar.setOnClickListener {
            if (util.isConnected(context!!) != true) {
                util.toast(context!!, "Verifique sua conexão com a internet")
                return@setOnClickListener
            }

            postValues!!.put("nome", container.EdtNome.text.toString())
            postValues!!.put("email", container.EdtEmail.text.toString())
            postValues!!.put("celular", container.EdtCelular.text.toString())
            if (validaCampos(postValues!!)) {
                container.BtnSalvar.isEnabled = false
                container.BtnSalvar.setText("Aguarde");


                var fuel: FuelManager = FuelManager()
                fuel.post(util.URL_API + "updateFirme", toListOf(postValues))
                    .responseString { request, response, result ->
                        try {
                            if (response.statusCode == 200) {
                                val (res, error) = result
                                if (res != null) {
                                    val user: userModel = Gson().fromJson(
                                        res,
                                        userModel::class.java
                                    )
                                    userController(activity!!, context!!).save(user)
                                    util.toast(context!!, "Cadastro atualizado com sucesso!!")
                                } else {
                                    throw Exception("Erro ao tentar atualizar seu cadastro!")
                                }
                            } else if (response.statusCode == 400) {
                                throw Exception("Erro nos dados informados!")
                            } else {
                                throw Exception("Erro na requisição!")
                            }
                        } catch (e: Exception) {
                            util.toast(context!!, e.message.toString())
                        } finally {
                            container.BtnSalvar.isEnabled = true
                            container.BtnSalvar.setText("Salvar");
                        }
                    }
            }
        }


        return container;
    }


    private fun toListOf(postValues: HashMap<String, Any>): List<Pair<String, Any?>>? {


        return listOf(
            "id" to postValues.get("id"),
            "nome" to postValues.get("nome"),
            "email" to postValues.get("email"),
            "celular" to postValues.get("celular"),
            "atalaia" to postValues.get("atalaia"),
            "tribo" to postValues.get("tribo"),
            "id_igreja" to postValues.get("id_igreja")
        )

    }

    private fun validaCampos(postValues: HashMap<String, Any>): Boolean {
        if (postValues.get("nome").toString().isEmpty()) {
            util.toast(context!!, "Preencha o seu nome !!")
            return false
        }
        if (postValues.get("email").toString().isEmpty()) {
            util.toast(context!!, "Preencha o seu email!!")
            return false
        }
        if (postValues.get("celular").toString().isEmpty() || postValues.get("celular").toString().length != 11) {
            util.toast(context!!, "Preencha o número do seu celular!!")
            return false
        }

        if (postValues.get("id_igreja").toString() == "" || !postValues.contains("id_igreja")) {
            util.toast(context!!, "Selecione sua igreja !!")
            return false
        }

        if (postValues.get("tribo").toString() == "" || !postValues.contains("tribo")) {
            util.toast(context!!, "Selecione sua tribo !!")
            return false
        }

        if (postValues.get("atalaia").toString() == "") {
            util.toast(context!!, "Selecione se você é ou não atalaia !!")
            return false
        }

        return true
    }

    private fun resolveCmbIgreja(container: View?) {
        try {
            var fuel: FuelManager = FuelManager()

            fuel.get(util.URL_API + "listarIgrejas/")
                .responseObject(igrejaModel.Deserializer()) { request, response, result ->
                    val (igreja, err) = result


                    //nomesIgrejas.add("Selecione sua igreja")
                    //idsIgrejas.add(0)

                    igreja?.forEach { igreja ->
                        nomesIgrejas.add(igreja.nome)
                        idsIgrejas.add(igreja.id)
                    }


                    container!!.progressBar.visibility = View.GONE


                    val aa = ArrayAdapter(
                        context!!,
                        android.R.layout.simple_spinner_item,
                        nomesIgrejas
                    )

                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    container!!.cmbIgrejas!!.adapter = aa
                    container.cmbIgrejas.setSelection(aa.getPosition(user.igreja!!.nome))

                    container!!.cmbIgrejas.onItemSelectedListener =
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
                                    util.toast(context!!, e.message.toString())
                                }
                            }

                        }

                }
        } catch (e: Exception) {
            util.toast(context!!, e.message.toString())
        }
    }

    private fun resolveCmbTribo(container: View) {
        container.cmbTribo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (!container.cmbTribo.getSelectedItem().equals("Selecione sua tribo"))
                        postValues!!.put("tribo", container.cmbTribo.getSelectedItem().toString())
                }

            }
    }


}
