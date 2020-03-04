package com.connect.fju.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.connect.fju.R
import com.connect.fju.controllers.cardsController

/**
 * A simple [Fragment] subclass.
 */
class RuaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var container = inflater.inflate(R.layout.fragment_evg, container, false)
        cardsController(
            getActivity()!!,
            context!!
        ).showCards(container.findViewById(R.id.linearLayoutEVG), "Rua")

        return container;
    }


}
