package com.connect.fju.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.connect.fju.R
import com.connect.fju.adpters.jovemAdapter
import com.github.kittinunf.fuel.core.FuelManager
import com.connect.fju.controllers.userController
import kotlinx.android.synthetic.main.fragment_almas.view.*
import com.connect.fju.models.jovemModel
import kotlinx.android.synthetic.main.fragment_almas.*
import util.util


class AlmasFragment : Fragment() {
    var jovensAdapter: jovemAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_almas, container, false)
        try {

            val user = userController(activity!!, context!!).getUser()
            var fuel: FuelManager = FuelManager()


            if (util.isConnected(context!!) != true) {
                util.toast(context!!, "Verifique sua conexão com a internet")
                return v
            }

            fuel.get(util.URL_API + "listarJovens/" + user!!.igreja!!.id.toString() + "/" + user.id)
                .responseObject(jovemModel.Deserializer()) { request, response, result ->
                    val (jovem, err) = result
                    var jovens = ArrayList<jovemModel>()



                    jovem?.forEach { jovem ->
                        jovens.add(jovem)
                    }

                    jovensAdapter = jovemAdapter(context!!, jovens)


                    v.LstAlmas.adapter = jovensAdapter
                    v.progressBarAlmas.visibility = View.GONE
                    if (jovens.size == 0) {
                        v.TxtMsg.setText("Você ainda não evangelizou ninguém esse mês,\n mas não se preocupe o dia da próxima EVG está chegando ;)")
                        v.TxtMsg.visibility = View.VISIBLE
                    }

                }


        } catch (e: Exception) {
            util.toast(context!!, e.message.toString())
        } finally {
            return v
        }
    }


}

