package com.example.projeto_softinsa_mobile_app


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto_softinsa_mobile_app.login.Authorization
import com.example.projeto_softinsa_mobile_app.ForgotPass

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginbtn: Button
    private lateinit var signupLink: TextView
    private lateinit var forgotpassword: TextView
    private lateinit var remembermeCheckbox: CheckBox

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var isLoggedIn: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginbtn = findViewById(R.id.loginbtn)
        signupLink = findViewById(R.id.sigupLink)
        forgotpassword = findViewById(R.id.forgotPassword)
        remembermeCheckbox = findViewById(R.id.rememberMe)

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        remembermeCheckbox.isChecked = isLoggedIn

        if (isLoggedIn) {
            val savedEmail = sharedPreferences.getString("Email", "")
            val savedPassword = sharedPreferences.getString("Password", "")
            emailEditText.setText(savedEmail)
            passwordEditText.setText(savedPassword)
        } else {
            emailEditText.setText("") // Limpa o campo de email caso o usuário não esteja logado
        }

        remembermeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            isLoggedIn = isChecked
            editor.putBoolean("isLoggedIn", isChecked)

            val email = emailEditText.text.toString().trim() // Obter o valor atualizado do campo de email

            if (isChecked) {
                editor.putString("Email", email) // Armazenar o valor do campo de email se a opção "Remember Me" estiver marcada
            } else {
                editor.remove("Email") // Remover o valor do campo de email se a opção "Remember Me" não estiver marcada
            }

            editor.apply()
        }

        loginbtn.setOnClickListener {
            internetConnection()
        }

        signupLink.setOnClickListener {
            val intentSignUp = Intent(this, SignupActivity::class.java)
            startActivity(intentSignUp)
        }

        forgotpassword.setOnClickListener {
            val intentforgotPassword = Intent(this, ForgotPass::class.java)
            startActivity(intentforgotPassword)
        }
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
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val isPrimeiroLogin = true
            val authorization = Authorization(this, editor)

            val loginCallback = object : Authorization.LoginCallback {
                override fun onSuccess(token: String) {

                    /*if(isPrimeiroLogin == true)
                     {
                         val intent = Intent(this@MainActivity, ResetPassword::class.java)
                         intent.putExtra("userId", 123) // Substitua 123 pelo valor correto do userId
                         startActivity(intent)
                     }else
                     {*/
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    //}

                }

                override fun onFailure(errorMessage: String) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authorization.login(email, password, isPrimeiroLogin, loginCallback)
            } else {
                Toast.makeText(this, "Por favor, insira o email e a senha", Toast.LENGTH_SHORT).show()
            }
        }
    }
}