package com.connect.fju.activity


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.connect.fju.R
import com.connect.fju.controllers.userController
import com.connect.fju.fragments.*
import com.connect.fju.models.userModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.nav_header.*
import util.util


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var user: userModel? = null

    override fun onResume() {
        super.onResume()

        if (user == null) {
            logout()
        } else {
            //nomeMenu.setText(user!!.nome)

            when (user!!.tribo) {

                "Aser" -> {
                    logoTribo.setImageResource(R.drawable.aser)
                    true
                }

                "Benjamin" -> {
                    logoTribo.setImageResource(R.drawable.benjamin)
                    true
                }

                "Efraim" -> {
                    logoTribo.setImageResource(R.drawable.efraim)
                    true
                }

                "Gade" -> {
                    logoTribo.setImageResource(R.drawable.gade)
                    true
                }

                "Issacar" -> {
                    logoTribo.setImageResource(R.drawable.issacar)
                    true
                }

                "Judá" -> {
                    logoTribo.setImageResource(R.drawable.juda)
                    true
                }

                "Levi" -> {
                    logoTribo.setImageResource(R.drawable.levi)
                    true
                }

                "Manassés" -> {
                    logoTribo.setImageResource(R.drawable.manasses)
                    true
                }

                "Naftali" -> {
                    logoTribo.setImageResource(R.drawable.naftali)
                    true
                }

                "Rúben" -> {
                    logoTribo.setImageResource(R.drawable.ruben)
                    true
                }

                "Simeão" -> {
                    logoTribo.setImageResource(R.drawable.simeao)
                    true
                }

                "Zebulom" -> {
                    logoTribo.setImageResource(R.drawable.zebulom)
                    true
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        user = userController(this, baseContext).getUser()
        if (user == null) {
            logout()
        }

        util.verificarPermissoes(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        var drawer = drawer_layout
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {}

        drawer.addDrawerListener(toggle);
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(
                    R.id.fragment_container,
                    HomeFragment()
                ).commit();
            nav_view.setCheckedItem(R.id.nav_home);
            getSupportActionBar()?.setTitle("Home");
        }


        //nomeMenu.setText(user!!.nome)
        //emailMenu.setText(user!!.email)


    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }


        val count = supportFragmentManager.backStackEntryCount
        var titulo = ""
        var id = 0

        if (count > 1) {
            try {
                titulo = supportFragmentManager.getBackStackEntryAt(count - 2).getName().toString()
                when (titulo) {
                    "Home" -> {
                        id = R.id.nav_home
                        true
                    }
                    "Rua" -> {
                        id = R.id.nav_street
                        true
                    }
                    "Portas" -> {
                        id = R.id.nav_portas
                        true
                    }
                    "Almas" -> {
                        id = R.id.nav_portas
                        true
                    }
                    "Mensagens" -> {
                        id = R.id.nav_mensagens
                        true
                    }
                    "Perfil" -> {
                        id = R.id.nav_perfil
                        true
                    }
                }

            } catch (e: Exception) {

            }
        } else {
            id = R.id.nav_home
            titulo = "Home"
        }



        getSupportFragmentManager().popBackStack()

        nav_view.setCheckedItem(id);
        getSupportActionBar()?.setTitle(titulo);
    }

    override fun onNavigationItemSelected(it: MenuItem): Boolean {
        var titulo = ""
        when (it.itemId) {
            R.id.nav_home -> {
                titulo = "Home";
                getSupportFragmentManager().beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        HomeFragment()
                    ).addToBackStack(titulo).commit();

                true
            }

            R.id.nav_portas -> {
                titulo = "Portas";
                getSupportFragmentManager().beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PortasFragment()
                    ).addToBackStack(titulo).commit();

                true
            }

            R.id.nav_street -> {
                titulo = "Rua";
                getSupportFragmentManager().beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        RuaFragment()
                    ).addToBackStack(titulo).commit();

                true
            }

            R.id.nav_almas -> {
                titulo = "Almas";
                getSupportFragmentManager().beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        AlmasFragment()
                    ).addToBackStack(titulo).commit();

                true
            }

            R.id.nav_mensagens -> {
                titulo = "Mensagens";
                getSupportFragmentManager().beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        MensagensFragment()
                    ).addToBackStack(titulo).commit();

                true
            }

            R.id.nav_perfil -> {
                titulo = "Perfil";
                getSupportFragmentManager().beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        PerfilFragment()
                    ).addToBackStack(titulo).commit();

                true
            }

            R.id.share -> {
                val shareBody = getString(R.string.share_body)
                val sharingIntent = Intent(Intent.ACTION_SEND)

                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.share_subject)
                )

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)))
            }

            R.id.nav_logout -> {
                logout()
            }

            else -> false

        }


        getSupportActionBar()?.setTitle(titulo);
        nav_view.setCheckedItem(it.itemId);

        drawer_layout.closeDrawer(GravityCompat.START, true);
        return true
    }

    private fun logout() {
        val settings: SharedPreferences =
            baseContext.getSharedPreferences("usuario", Context.MODE_PRIVATE)
        settings.edit().clear().commit()


        var intent = Intent(applicationContext, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }


    fun goTo(fragment: String, value: String = "") {
        when (fragment) {
            "Home" -> {
                getSupportFragmentManager()!!.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        HomeFragment()
                    ).commit()
                nav_view.setCheckedItem(R.id.nav_home);
            }

            "Alma" -> {
                val bundle = Bundle()
                bundle.putString("jovem", value)
                val almaFragment = AlmaFragment()
                almaFragment.arguments = bundle


                getSupportFragmentManager()!!
                    .beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        almaFragment
                    ).commit()
                nav_view.setCheckedItem(0);
            }

            "Share" -> {
                val bundle = Bundle()
                bundle.putString("jovem", value)
                val shareFragment = ShareFragment()
                shareFragment.arguments = bundle

                getSupportFragmentManager()!!.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        shareFragment
                    ).commit()
                nav_view.setCheckedItem(0);
                true
            }
        }

        getSupportActionBar()?.setTitle(fragment);

    }

}
