package com.connect.fju.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.connect.fju.R
import com.connect.fju.controllers.userController
import com.connect.fju.models.userModel
import kotlinx.android.synthetic.main.activity_login.*
import  util.util

class LoginActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        btnLogin.isEnabled = true
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        util.verificarPermissoes(this)

        if (util.isConnected(baseContext) != true) {
            util.toast(baseContext, "Verifique sua conexão com a internet")
        }


        // FCMService.getInstance().isAutoInitEnabled = true;
        if (userController(this, baseContext).getUser() != null) {
            userController.userLogin()
        }


        btnLogin.setOnClickListener {
            if (util.isConnected(baseContext) != true) {
                util.toast(baseContext, "Verifique sua conexão com a internet")
                return@setOnClickListener
            }

            val user =
                userModel(celular = EdtCelular.text.toString(), senha = EdtSenha.text.toString())


            userController(this, baseContext).login(user)
        }

        BtnGoToCadastro.setOnClickListener {
            var intent = Intent(applicationContext, CadastroActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }

        BtnEsqueciSenha.setOnClickListener {
            var intent = Intent(applicationContext, EsqueciSenhaActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }
    }
}
