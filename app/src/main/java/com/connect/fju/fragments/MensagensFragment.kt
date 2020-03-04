package com.connect.fju.fragments


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.connect.fju.R
import com.connect.fju.activity.MainActivity
import com.connect.fju.controllers.cardsController
import kotlinx.android.synthetic.main.fragment_share.view.*
import com.connect.fju.models.jovemModel
import com.connect.fju.models.userModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_mensagens.*
import kotlinx.android.synthetic.main.fragment_mensagens.view.*
import util.util


class MensagensFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_mensagens, container, false)


        var msgWPP =
            util.getMessage(context!!, "welcomeWPP")

        var msgSMS =
            util.getMessage(context!!, "welcomeSMS")


        v.EdtMsgSMS.setText(msgSMS)
        v.EdtMsgWPP.setText(msgWPP)

        v.BtnSalvar.setOnClickListener {
            BtnSalvar.isEnabled =false
            BtnSalvar.setText("Aguarde.")
            if (v.EdtMsgSMS.text!!.isNotEmpty() && v.EdtMsgWPP.text!!.isNotEmpty()) {
                util.saveMessage(context!!, "welcomeWPP", v.EdtMsgWPP.text!!.toString())
                util.saveMessage(context!!, "welcomeSMS", v.EdtMsgSMS.text!!.toString())
                util.toast(context!!, "Mensagens salvas com sucesso!")
            } else {
                util.toast(context!!, "Preencha os campos corretamente!")
            }
            BtnSalvar.isEnabled =true
            BtnSalvar.setText("Salvar")
        }
        return v
    }
}
