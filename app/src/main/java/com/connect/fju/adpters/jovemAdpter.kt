package com.connect.fju.adpters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.connect.fju.R
import com.connect.fju.activity.MainActivity
import com.connect.fju.controllers.cardsController
import com.connect.fju.models.jovemModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.alma_row.view.*
import util.util
import java.lang.Exception


class jovemAdapter : BaseAdapter {
    lateinit var jovens: ArrayList<jovemModel>
    var context: Context

    constructor(context: Context, jovens: ArrayList<jovemModel>) : super() {
        this.jovens = jovens
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val jovem = jovens[position]
        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflator.inflate(R.layout.alma_row, null)
        view.TxtNome.text = jovem.nome.toString()
        view.TxtCelular.text = jovem.celular.toString()
        view.TxtLocalEVG.text = jovem.localEVG.toString()
        view.LinearLayoutItem.setOnClickListener {
            try {

                val activity = (it.context) as Activity
                cardsController(activity, context).save(jovem)
                (activity as MainActivity).goTo("Alma", Gson().toJson(jovem).toString())
            } catch (e: Exception) {
                util.toast(it.context, e.message.toString())
            }

        }
        return view
    }

    override fun getItem(position: Int): Any {
        return jovens[position]
    }

    override fun getItemId(position: Int): Long {
        return jovens[position].id.toLong()
    }

    override fun getCount(): Int {
        return jovens.size
    }

}