package com.example.projeto_softinsa_app

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.projeto_softinsa_app.login.Authorization
import com.example.projeto_softinsa_app.ForgotPass
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projeto_softinsa_app.Helpers.FileUtils
import com.example.projeto_softinsa_app.Helpers.JsonHelper
import com.example.projeto_softinsa_app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginbtn: Button
    private lateinit var signupLink: TextView
    private lateinit var forgotpassword: TextView
    private lateinit var remembermeCheckbox: CheckBox
    private lateinit var showpass: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isPrimeiroLogin: Boolean = false
    private var isLoggedIn: Boolean = false
    private var isGoogleSignIn: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginbtn = findViewById(R.id.loginbtn)
        signupLink = findViewById(R.id.sigupLink)
        forgotpassword = findViewById(R.id.forgotPassword)
        remembermeCheckbox = findViewById(R.id.rememberMe)
        showpass= findViewById(R.id.showpass_signin)
        Log.d("Tag", "Mensagem de log de depuração") // Usando a prioridade DEBUG
        Log.e("Tag", "Mensagem de erro") // Usando a prioridade ERROR
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

        showpass.setOnClickListener{
            val currentInputType = passwordEditText.inputType

            if (currentInputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // se a pass estiver visivel, muda para o modo normal
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showpass.setImageResource(R.drawable.baseline_see_pass)
            } else {
                // If the password is currently hidden, switch to visible password mode
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showpass.setImageResource(R.drawable.baseline_see_pass)
            }

            // Move the cursor to the end of the text after changing input type
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Login with google
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        findViewById<SignInButton>(R.id.loginGoogleBtn).setOnClickListener {
            isGoogleSignIn = true
            editor.putBoolean("isGoogleSignIn", isGoogleSignIn)
            editor.apply()
            val isGoogleSignIn = sharedPreferences.getBoolean("isGoogleSignIn", false)
            Log.d("isGoogleSignIn", isGoogleSignIn.toString())
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        } else {
            System.out.println("Hello, " + result.resultCode);
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                editor.putInt("userId", 1)

                Log.d("account google name", account.displayName.toString())
                val delimiter = " "
                val parts = account.displayName.toString().split(delimiter)
                val primeiroNome: String = parts[0]
                val ultimoNome: String = parts[1]
                val file = File(this.filesDir, "users.json")
                if (file.exists()){
                    file.delete()
                }

                val body = JSONObject()
                val cargoObjeto = JSONObject()

                try {
                    body.put("userId", 1)
                    body.put("googleId", 1)
                    body.put("primeiroNome", primeiroNome)
                    body.put("ultimoNome", ultimoNome)
                    body.put("numeroFuncionario", 1)
                    body.put("email", account.email)
                    body.put("password", "123")
                    body.put("telemovel", "912345678")
                    body.put("morada", "Rua 1")
                    body.put("salario", 2000)
                    body.put("ultimoLogin", JsonHelper.getCurrentDateFormatted())
                    body.put("dataContratacao", JsonHelper.getCurrentDateFormatted())
                    body.put("dataRegisto", JsonHelper.getCurrentDateFormatted())
                    body.put("isPrimeiroLogin", false)
                    body.put("isAtivo", true)
                    body.put("isColaborador", true)
                    body.put("isCandidato", false)
                    body.put("verificationToken", "")
                    body.put("recoverToken", "")
                    cargoObjeto.put("cargoId", 1)
                    cargoObjeto.put("cargoNome", "Colaborador")
                    body.put("cargo", cargoObjeto)
                    body.put("departamentoId", 1)
                    body.put("filialId", 1)

                    JsonHelper.addToJsonFile(this@MainActivity, "users.json", body, "users")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                Authorization.saveUserId(this, 1)
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.putExtra("userId", account.id)
                intent.putExtra("email", account.email)
                // intent.putExtra("isServerPrimeiroLogin", isServerPrimeiroLogin)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
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
            val authorization = Authorization(this, editor)
            val loginCallback = object : Authorization.LoginCallback {
                override fun onSuccess(token: String) {
                    val userId = authorization.getUserId()
                    val isServerPrimeiroLogin = authorization.getIsServerPrimeiroLogin()
                    Log.d("MainActivity", "isServerPrimeiroLogin: $isServerPrimeiroLogin")

                    if (isServerPrimeiroLogin) {
                        val intent = Intent(this@MainActivity, ResetPassword::class.java)
                        intent.putExtra("userId", userId)
                        intent.putExtra("email", email)
                        intent.putExtra("isServerPrimeiroLogin", isServerPrimeiroLogin)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                        startActivity(intent)
                    }
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