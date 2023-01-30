package mx.erick.araitastyles

import android.R
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import mx.erick.araitastyles.databinding.ActivityLoginBinding
import mx.erick.araitastyles.ui.slideshow.SlideshowFragment


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isConnected) {
            // Si hay conexión a Internet en este momento
        } else {
            //Para cerrar la app
            alertInternet()
        }

        validate()
        sesiones()
    }

    private fun sesiones(){
        val preferencias =
            getSharedPreferences("mx.erick.araitastyles.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        var email: String? = preferencias.getString("email", null)
        var provedor: String? = preferencias.getString("provedor", null)
        if (email != null && provedor != null) {
            opciones(email, TipoProvedor.valueOf(provedor))
        }
    }

    private fun validate(){

        //por correo
        binding.updateUser.setOnClickListener {
            if (!binding.username.text.toString().isEmpty() && !binding.password.text.toString()
                    .isEmpty()
            ) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener {
                    if (it.isComplete) {
                        try {
                            opciones(it.result?.user?.email ?: "", TipoProvedor.CORREO)
                        } catch (e: Exception) {
                            alert()
                        }
                    } else {
                        alert()
                    }
                }
            }
        }

        //establecer enlace
        binding.loginbtn.setOnClickListener {
            if (!binding.username.text.toString().isEmpty() && !binding.password.text.toString()
                    .isEmpty()
            ) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        println("llegamos ---------------")
                        opciones(it.result?.user?.email ?: "", TipoProvedor.CORREO)
                    } else {
                        alert()
                    }
                }
            }
        }

        //google acceso
        iniciarActividad()
        binding.google.setOnClickListener {
            val conf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
                    "631838642859-oh2tl52b19k32n7uaofq4hln4e4sj45e.apps.googleusercontent.com"
                )
                    .requestEmail()
                    .build()
            val clienteGoogle = GoogleSignIn.getClient(this, conf)
            clienteGoogle.signOut()
            val signIn: Intent = clienteGoogle.signInIntent
            activityResultLauncher.launch(signIn)
        }

    }

    private fun opciones(email: String, provesor: TipoProvedor) {
        var pasos: Intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provedor", provesor.name)

            val preferencias = getSharedPreferences(getString(mx.erick.araitastyles.R.string.file_preferencia), Context.MODE_PRIVATE).edit()
            preferencias.putString("email", email)
            preferencias.putString("provedor", provesor.name)
            preferencias.commit()
        }
        startActivity(pasos)
    }

    private fun iniciarActividad() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                        if (account != null) {
                            val credenciales =
                                GoogleAuthProvider.getCredential(account.idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(credenciales)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        opciones(account.email ?: "", TipoProvedor.GOOGLE)
                                    } else {
                                        alert()
                                    }
                                }
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(this, "Sign in failed: " + e.statusCode, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun alert() {
        val bulder = AlertDialog.Builder(this)
        bulder.setTitle("Mensaje")
        bulder.setMessage("Datos incorrectos")
        bulder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = bulder.create()
        dialog.show()
        binding.username.setText("")
        binding.password.setText("")
    }

    override fun onStart() {
        super.onStart()
        binding.layoutAcceso.visibility = View.VISIBLE
    }

    private fun alertInternet() {
        val bulder = AlertDialog.Builder(this)
        bulder.setTitle("Mensaje")
        bulder.setMessage("No tienes conexión a internet")
        bulder.setPositiveButton("Aceptar", DialogInterface.OnClickListener(function = possitiveButton))
        val dialog: AlertDialog = bulder.create()
        dialog.show()
    }

    //Matar la app
    val possitiveButton = { dialog: DialogInterface, which: Int ->
        finishAffinity()
    }
}