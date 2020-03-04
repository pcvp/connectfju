package com.connect.fju.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.connect.fju.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_home, container, false)
        try {

            v.BtnPortas.setOnClickListener {
                try {
                    getFragmentManager()!!.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            PortasFragment()
                        ).addToBackStack(null).commit();
                    (activity as AppCompatActivity).supportActionBar?.title = "Portas"
                    (activity as AppCompatActivity).nav_view.setCheckedItem(R.id.nav_portas);
                } catch (e: Exception) {
                    Log.d("TAG123", e.message)
                }
            }

            v.BtnRua.setOnClickListener {
                try {
                    getFragmentManager()!!.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            RuaFragment()
                        ).addToBackStack(null).commit();
                    (activity as AppCompatActivity).supportActionBar?.title = "Rua"
                    (activity as AppCompatActivity).nav_view.setCheckedItem(R.id.nav_street);
                } catch (e: Exception) {
                    Log.d("TAG123", e.message)
                }
            }

        } catch (e: Exception) {
            Log.d("TAG123", e.message)
        } finally {
            return v
        }
    }


}
