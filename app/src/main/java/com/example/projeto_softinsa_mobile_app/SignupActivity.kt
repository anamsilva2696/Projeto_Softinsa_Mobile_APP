package com.example.projeto_softinsa_mobile_app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.projeto_softinsa_mobile_app.login.Authorization


class SignupActivity : AppCompatActivity() {
    private lateinit var signupButton: Button
    private lateinit var signinLink: TextView
    private lateinit var authorization: Authorization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupButton = findViewById(R.id.signupbtn)
        signinLink = findViewById(R.id.signinLink)
        authorization = Authorization(this, getSharedPreferences("YourPrefs", MODE_PRIVATE).edit())

        signupButton.setOnClickListener {
            internetConnection()
        }

        signinLink.setOnClickListener{
            val signinIntent = Intent(this@SignupActivity, MainActivity::class.java)
            startActivity(signinIntent)
        }

    }
    // Função para exibir um Toast
    private fun showToast(message: String) {
        Toast.makeText(this@SignupActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    private fun internetConnection() {
        if (!checkForInternet(this)) {
            val positiveButtonClick = { dialog: DialogInterface, _: Int ->
                internetConnection()
                dialog.dismiss()
            }

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Erro de conexão à Internet")
            alertDialogBuilder.setMessage("Não está conectado à internet!\nTente novamente quando estiver conectado")
            alertDialogBuilder.setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener(positiveButtonClick)
            )
            alertDialogBuilder.show()
        } else {
            val firstName = findViewById<EditText>(R.id.fname).text.toString()
            val lastName = findViewById<EditText>(R.id.lname).text.toString()
            val email = findViewById<EditText>(R.id.email).text.toString()
            val phone = findViewById<EditText>(R.id.telefone).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            val isPrimeiroLogin = true

            //implementar uma verificação para so aceitar o primeiro nome escrito e cagar no q vem a seguir no mesmo edit text :3!!!!!!!!
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (password.matches(Regex(".*[A-Z].*")) && password.matches(Regex(".*\\d.*"))) {
                    authorization.registar(firstName, lastName, phone, email, password, isPrimeiroLogin, object : Authorization.LoginCallback {
                        override fun onSuccess(token: String) {
                            showToast("Registration successful. Please check your email for further instructions.")
                            // Aqui você pode redirecionar o usuário para a próxima tela, por exemplo.
                        }

                        override fun onFailure(errorMessage: String) {
                            showToast("Registration failed: $errorMessage")
                        }
                    })
                } else {
                    Toast.makeText(this, "A senha deve conter pelo menos um caractere maiúsculo e um número.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, insira todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}