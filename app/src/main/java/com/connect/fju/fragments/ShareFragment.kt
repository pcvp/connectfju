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
import util.util


class ShareFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_share, container, false)
        try {


            val bundle = this.arguments
            if (bundle != null) {
                var jovem = Gson().fromJson(
                    bundle.getString("jovem", "{}").toString(),
                    jovemModel::class.java
                )


                var msgWPP = util.getMessage(context!!, "welcomeWPP").replace("*nomedojovem*", jovem.nome)
                    .replace("*meunome*", util.getUser(context!!).nome)

                var msgSMS = util.getMessage(context!!, "welcomeSMS").replace("*nomedojovem*", jovem.nome)
                    .replace("*meunome*", util.getUser(context!!).nome)

                v.BtnWPP.setOnClickListener {
                    try {
                        util.openWPP(activity!!, context!!, jovem!!.celular, msgWPP)
                        (activity as MainActivity).goTo("Home")
                    } catch (e: Exception) {
                        util.toast(context!!, e.message.toString())
                    }
                }

                v.BtnSMS.setOnClickListener {
                    val uri = Uri.parse("smsto:" + jovem.celular)
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    intent.putExtra("sms_body", msgSMS)
                    startActivity(intent)

                    (activity as MainActivity).goTo("Home")
                }
            }

        } catch (e: Exception) {
            util.toast(context!!, e.message.toString())
        } finally {
            return v
        }
    }


}
