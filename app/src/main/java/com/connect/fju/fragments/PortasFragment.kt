package com.connect.fju.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.connect.fju.R
import com.connect.fju.controllers.cardsController
import util.util


class PortasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var container = inflater.inflate(R.layout.fragment_evg, container, false)
        cardsController(
            getActivity()!!,
            context!!
        ).showCards(container.findViewById(R.id.linearLayoutEVG), "Portas")


        return container;
    }
}