package com.connect.fju.controllers

import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.fragment_evg.*
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.connect.fju.R
import com.connect.fju.activity.MainActivity
import com.github.kittinunf.fuel.core.FuelManager
import com.google.gson.Gson
import com.connect.fju.models.camposModel
import com.connect.fju.models.cardsModel
import com.connect.fju.models.jovemModel
import com.connect.fju.models.userModel
import util.util
import java.util.*
import kotlin.collections.HashMap


val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

class cardsController(
    paramterActivity: Activity,
    paramterContext: Context
) {

    private var jovem: jovemModel? = null

    companion object {


        private lateinit var context: Context
        private lateinit var activity: Activity


        var top = 0;

        lateinit var ll: LinearLayout
        lateinit var evg: String;

        var opcoes: HashMap<String, Any> = HashMap<String, Any>() //define empty hashmap
        var localEVG = ""

    }

    init {
        context = paramterContext
        activity = paramterActivity
    }

    fun showCards(ll_parameter: LinearLayout?, evg_parameter: String, jovem: jovemModel? = null) {
        evg = evg_parameter!!;
        ll = ll_parameter!!



        try {
            val user: userModel = util.getUser(context)


            if (jovem == null) {
                addToView(user.igreja!!.cards)
                if (evg == "Rua") {
                    val card = addCard("EVG")

                    val txtLocal = addEditText("Local da EVG")

                    card.addView(txtLocal)
                    ll.addView(card)
                } else {
                    localEVG = "Portas"
                }
                addBtnSalvar()
            } else {
                this.jovem = jovem




                addToView(user.igreja!!.cards)
                val card = addCard("EVG")
                val txtLocal = addEditText("Local da EVG")
                card.addView(txtLocal)
                ll.addView(card)
                addBtnWPP()
            }

        } catch (e: Exception) {
            util.toast(context, e.message.toString())
        }
    }

    private fun addEditText(s: String): EditText {
        var editText = EditText(context)


        editText.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )


        if (this.jovem != null) {
            editText.setText(this.jovem!!.localEVG)
            if (this.jovem!!.localEVG == "Portas") {
                editText.isEnabled = false
            }
        }

        editText.setHint("Local da EVG");
        editText.setPadding(25, 25, 25, 25)


        var btnMarginParams =
            editText.getLayoutParams() as ViewGroup.MarginLayoutParams
        btnMarginParams.setMargins(15, 30.px, 15, 10.px)
        editText.requestLayout()

        editText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    localEVG = s.toString()
                }

            }
        )
        return editText;
    }

    private fun addBtnWPP() {
        var btnWPP = Button(context)

        btnWPP.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        btnWPP.setBackgroundColor(ContextCompat.getColor(context, R.color.fju_vermelho))
        btnWPP.setTextColor(ContextCompat.getColor(context, R.color.white))
        btnWPP.setText("Abrir conversa no WPP");
        btnWPP.setPadding(25, 25, 25, 25)


        var btnMarginParams =
            btnWPP.getLayoutParams() as ViewGroup.MarginLayoutParams
        btnMarginParams.setMargins(15, 5.px, 15, 10.px)
        btnWPP.requestLayout()


        btnWPP.setOnClickListener {


            try {
                util.openWPP(activity!!, context!!, jovem!!.celular)
            } catch (e: Exception) {
                util.toast(context!!, e.message.toString())
            }
        }

        ll.addView(btnWPP)

    }

    private fun addBtnSalvar() {
        var btnSalvar = Button(context)

        btnSalvar.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        btnSalvar.setText("Salvar");
        btnSalvar.setPadding(25, 25, 25, 25)


        var btnMarginParams =
            btnSalvar.getLayoutParams() as ViewGroup.MarginLayoutParams
        btnMarginParams.setMargins(15, 5.px, 15, 10.px)
        btnSalvar.requestLayout()


        btnSalvar.setOnClickListener {
            if (util.isConnected(context!!) != true) {
                util.toast(context!!, "Verifique sua conexão com a internet")
                return@setOnClickListener
            }

            if (validaCampos()) {

                val user = userController(activity, context).getUser()!!

                btnSalvar.isEnabled = false
                btnSalvar.setText("Aguarde");
                val jovem = jovemModel(
                    nome = activity.EdtNome.text.toString(),
                    sobrenome = activity.EdtSobrenome.text.toString(),
                    nascimento = activity.EdtNascimento.text.toString(),
                    bairro = activity.EdtBairro.text.toString(),
                    celular = activity.EdtCelular.text.toString(),
                    opcoes = Gson().toJson(opcoes),
                    tribo = user.tribo,
                    localEVG = localEVG,
                    id_firme = user.id,
                    id_igreja = user.igreja!!.id
                );

                var fuel: FuelManager = FuelManager()
                fuel.post(util.URL_API + "addJovem", toListOf(jovem))
                    .responseString { request, response, result ->
                        try {
                            if (response.statusCode == 200) {
                                val (res, error) = result
                                if (res != null) {
                                    var dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                                        .toString();
                                    if (dia.length == 1) {
                                        dia = "0" + dia
                                    }

                                    var mes = (Calendar.getInstance().get(Calendar.MONTH) + 1)
                                        .toString();
                                    if (mes.length == 1) {
                                        mes = "0" + mes
                                    }

                                    util.addContact(
                                        activity!!,
                                        context!!,
                                        jovem.nome,
                                        jovem.localEVG + " - Dia " + dia + "/" + mes,
                                        jovem.celular
                                    )
                                    (activity as MainActivity).goTo("Share", res)
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
                            util.toast(cardsController.context, e.message.toString())
                        } finally {
                            btnSalvar.isEnabled = true
                            btnSalvar.setText("Salvar");
                        }
                    }
            }
        }

        ll.addView(btnSalvar)

    }

    private fun validaCampos(): Boolean {
        var msg = ""
        if (activity.EdtNome.text.toString().length == 0) {
            msg = "Digite o nome do jovem!"
        } else if (activity.EdtNascimento.text.toString().length == 0) {
            msg =
                "Digite o dia/mês do nascimento. Ou digite 01/01 para prosseguir sem essa informação."
        } else if (activity.EdtCelular.text.toString().length == 0) {
            msg = "Digite o número de celular do jovem!"
        }

        if (evg == "Rua" && localEVG == "") {
            msg = "Digite o local da evangelização!"
        }

        if (msg == "")
            return true
        else {
            util.toast(context, msg)
            return false
        }
    }

    fun save(jovem: jovemModel): Boolean {
        if (jovem != null) {
            try {

                var prefs = cardsController.context.getSharedPreferences(
                    "jovem",
                    Context.MODE_PRIVATE
                )
                var editor = prefs!!.edit()
                editor.putString("jovem", Gson().toJson(jovem))
                editor.apply()
            } catch (e: Exception) {
                util.toast(
                    cardsController.context,
                    e.message.toString()
                )
                return false
            } finally {
                return true
            }
        } else {
            return false
        }
    }


    fun getJovem(): jovemModel? {
        var prefs = cardsController.context.getSharedPreferences("jovem", Context.MODE_PRIVATE);
        if (prefs.contains("jovem"))
            return Gson().fromJson(
                prefs.getString("jovem", null),
                jovemModel::class.java
            )
        else
            return null
    }

    private fun toHashMap(jovem: jovemModel): HashMap<String, Any> {
        var hashMap: HashMap<String, Any> = HashMap<String, Any>()
        hashMap.put("id", jovem.id)
        hashMap.put("nome", jovem.nome)
        hashMap.put("sobrenome", jovem.sobrenome)
        hashMap.put("nascimento", jovem.nascimento)
        hashMap.put("celular", jovem.celular)
        hashMap.put("bairro", jovem.bairro)
        hashMap.put("referencia", jovem.referencia)
        hashMap.put("opcoes", jovem.opcoes)
        hashMap.put("tribo", jovem.tribo)
        hashMap.put("dataEVG", jovem.dataEVG)
        hashMap.put("localEVG", jovem.localEVG)
        hashMap.put("id_firme", jovem.id_firme.toString())
        hashMap.put("id_igreja", jovem.id_igreja.toString())

        return hashMap;
    }


    private fun toListOf(jovem: jovemModel): List<Pair<String, Any?>>? {
        return listOf(
            "id" to jovem.id,
            "nome" to jovem.nome,
            "sobrenome" to jovem.sobrenome,
            "nascimento" to jovem.nascimento,
            "celular" to jovem.celular,
            "bairro" to jovem.bairro,
            "referencia" to jovem.referencia,
            "opcoes" to jovem.opcoes,
            "tribo" to jovem.tribo,
            "dataEVG" to jovem.dataEVG,
            "localEVG" to jovem.localEVG,
            "id_firme" to jovem.id_firme,
            "id_igreja" to jovem.id_igreja
        )
    }

    private fun addToView(cardsArray: List<cardsModel>) {
        cardsArray.forEach {
            ll?.addView(addCard(it.titulo, it.fields));
        }

    }

    private fun addTextView(text: String, titulo: Boolean = false): TextView {
        var textview = TextView(context)

        textview.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        textview.setText(text)
        var chkMarginParams =
            textview.getLayoutParams() as ViewGroup.MarginLayoutParams

        if (titulo) {
            textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14F)
            textview.setTextColor(ContextCompat.getColor(context, R.color.lblCards))
            textview.setPadding(25, 25, 25, 35)
            textview.setTypeface(Typeface.DEFAULT_BOLD);
            textview.setGravity(Gravity.CENTER)
            chkMarginParams.setMargins(5.px, top.px, 15, 15)
        } else {
            top += 18;
            chkMarginParams.setMargins(37.px, top.px, 15, 15)
            textview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12F)
            textview.setTextColor(ContextCompat.getColor(context, R.color.lblCards))
            textview.setPadding(10.px, 25, 25, 25)
        }



        textview.requestLayout()

        return textview;
    }

    private fun addChk(titulo: String, slug: String): CheckBox {
        var chk = CheckBox(context)

        chk.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )


        chk.setText(titulo);
        chk.setPadding(25, 25, 25, 25)

        Log.d("!@#", top.toString());
        if (top == 0)
            top += 40
        else
            top += 30;
        var chkMarginParams =
            chk.getLayoutParams() as ViewGroup.MarginLayoutParams
        chkMarginParams.setMargins(15, top.px, 15, 15)
        chk.requestLayout()


        chk.setOnClickListener {
            if (opcoes.containsKey(slug)) {
                opcoes.remove(slug);
            } else {
                opcoes.put(slug, true)
            }
            Log.d("Opcoes", opcoes.toString())
        }


        if (this.jovem != null) {
            if (this.jovem!!.opcoes.toString().contains("\"$slug\": true")) {
                chk.isChecked = true
            }
        }

        return chk;
    }

    private fun addCard(titulo: String, fields: List<camposModel>? = null): CardView {
        top = 0;
        var cardview = CardView(context!!)

        val layoutparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        cardview.setLayoutParams(layoutparams)

        cardview.setPadding(25, 25, 25, 25)

        val cardViewMarginParams =
            cardview.getLayoutParams() as ViewGroup.MarginLayoutParams
        cardViewMarginParams.setMargins(15, 0, 15, 15)
        cardview.requestLayout()


        cardview.setCardBackgroundColor(Color.WHITE)

        cardview.setMaxCardElevation(30F)


        cardview.addView(addTextView(titulo, true))


        if (fields != null) {
            fields.forEach {
                cardview.addView(addChk(it.titulo, it.slug));
                cardview.addView(addTextView(it.horario))
            }

        }

        return cardview
    }
}

