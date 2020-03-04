package com.connect.fju.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.connect.fju.R
import com.connect.fju.controllers.cardsController
import com.connect.fju.models.jovemModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_evg.*


class AlmaFragment : Fragment() {
    lateinit var jovem: jovemModel

    override fun onResume() {
        super.onResume()
        val activity = getActivity()!!
        if (jovem != null) {
            activity.EdtNome.setText(jovem.nome)
            activity.EdtSobrenome.setText(jovem.sobrenome)
            activity.EdtNascimento.setText(jovem.nascimento)
            activity.EdtCelular.setText(jovem.celular)
            activity.EdtBairro.setText(jovem.bairro)
            activity.EdtReferencia.setText(jovem.referencia)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var container = inflater.inflate(R.layout.fragment_evg, container, false)
        val controller = cardsController(
            getActivity()!!,
            context!!
        )

        val bundle = this.arguments
        if (bundle != null) {
            jovem = Gson().fromJson(
                bundle.getString("jovem", "{}").toString(),
                jovemModel::class.java
            )

            controller.showCards(
                container.findViewById(R.id.linearLayoutEVG),
                "",
                jovem
            )
        }





        return container;
    }
}