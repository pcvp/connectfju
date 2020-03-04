package util

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.connect.fju.R
import com.connect.fju.activity.MainActivity
import com.connect.fju.controllers.cardsController
import com.connect.fju.models.userModel
import com.google.gson.Gson
import java.lang.Exception

class util {
    companion object {
        val URL_API: String = "https://api.connect-fju.com.br/v2/"
        val SEND_SMS_CODE: Int = 1
        val INTERNET: Int = 2
        val WRITECONTACTS: Int = 3
        val ACCESS_NETWORK_STATE: Int = 4


        fun isConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (cm != null) {
                val ni = cm.activeNetworkInfo

                return ni != null && ni.isConnected
            }

            return false
        }

        fun saveMessage(context: Context, slug: String, msg: String = "") {
            try {

                var prefs = context.getSharedPreferences(
                    "mensagens",
                    Context.MODE_PRIVATE
                )
                var editor = prefs!!.edit()
                editor.putString(slug, msg)
                editor.apply()
            } catch (e: Exception) {
                util.toast(
                    context,
                    e.message.toString()
                )
            }
        }

        fun getMessage(context: Context, slug: String): String {
            return context.getSharedPreferences("mensagens", Context.MODE_PRIVATE)
                .getString(slug, "").toString()
        }

        fun getUser(context: Context): userModel {
            val json: String = context!!.getSharedPreferences("usuario", Context.MODE_PRIVATE)
                .getString("usuario", "{}").toString()

            return Gson().fromJson(
                json,
                userModel::class.java
            )
        }

        fun openWPP(
            activity: Activity,
            context: Context,
            celular: String,
            texto: String = "",
            goHome: Boolean = true
        ) {
            val isAppInstalled = appInstalledOrNot(activity, "com.whatsapp")
            if (isAppInstalled) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://api.whatsapp.com/send?phone=+55" + celular.toString() +
                                "&text=" + texto
                    )
                )
                context.startActivity(intent)
                if (goHome)
                    (activity as MainActivity).goTo("Home")
            } else {
                util.toast(context!!, "Whatsapp não está instalado!")
            }
        }

        private fun appInstalledOrNot(activity: Activity, uri: String): Boolean {
            val pm = activity.packageManager
            return try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        fun toast(context: Context, msg: Any) {
            Toast.makeText(context, msg.toString(), Toast.LENGTH_LONG).show()
        }

        fun appPath(context: Context): String {
            return context.getFilesDir().getPath().toString()
        }

        fun alert(context: Context, msg: String = "Aguarde") {
            try {
                // Initialize a new instance of
                val builder = AlertDialog.Builder(context!!)

                // Set the alert dialog title
                builder.setTitle(R.string.app_name)

                // Display a message on alert dialog
                builder.setMessage(msg)


                // Display a neutral button on alert dialog
                builder.setNeutralButton("Ok") { _, _ ->
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } catch (e: Exception) {
                toast(context, e.message.toString())
            }

        }

        fun addContact(
            activity: Activity,
            context: Context,
            nome: String,
            complemento: String,
            numero: String
        ) {

            if (verificarPermissoesSalvarContato(activity)) {

                val ops = ArrayList<ContentProviderOperation>()

                val rawContactID: Int = ops.size


                // Adding insert operation to operations list
                // to insert a new raw contact in the table ContactsContract.RawContacts
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build()
                );

                // Adding insert operation to operations list
                // to insert display name in the table ContactsContract.Data
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                        )
                        .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            nome + " - EVG " + complemento
                        )
                        .build()
                );

                // Adding insert operation to operations list
                // to insert Mobile Number in the table ContactsContract.Data
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numero)
                        .withValue(
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                        )
                        .build()
                );

                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }
        }


        private fun verificarPermissoesSalvarContato(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED

        }

        fun verificarPermissoes(activity: Activity) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.SEND_SMS),
                    util.SEND_SMS_CODE
                )
            }


            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.INTERNET),
                    util.INTERNET
                )
            }

            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_NETWORK_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                    util.ACCESS_NETWORK_STATE
                )
            }

            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_CONTACTS),
                    util.WRITECONTACTS
                )
            }
        }
    }


}