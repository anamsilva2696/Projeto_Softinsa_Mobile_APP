package com.example.projeto_softinsa_mobile_app
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.projeto_softinsa_mobile_app.login.Authorization

class ResetPassword : AppCompatActivity() {
    private lateinit var resetbtn: Button
    private lateinit var signinLink: Button
    private lateinit var signupLink: Button

    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        resetbtn = findViewById(R.id.resetbtn)
        signinLink = findViewById(R.id.signinLink)
        signupLink = findViewById(R.id.signupLink)

        val email = findViewById<EditText>(R.id.email_change)
        val newPassword = findViewById<EditText>(R.id.confirmpassword_change)

        userId = intent.getIntExtra("userId", 0) // Obter o userId do Intent
        /*if (userId == 0) {
            // Lidar com o valor padrão do userId (0) aqui
            Toast.makeText(this, "Erro ao obter o ID do usuário", Toast.LENGTH_SHORT).show()
            finish() // Finalizar a atividade atual
            return // Encerrar a execução do método onCreate
        }*/
        resetbtn.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            val sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val authorization = Authorization(this, editor)
            alertDialogBuilder.setTitle("Confirmação")
            alertDialogBuilder.setMessage("Tem certeza de que alterar pass do seu perfil? Essa ação não pode ser desfeita!")
            alertDialogBuilder.setPositiveButton("Sim") { dialog, _ ->
                authorization.ChangePassword(userId, email.text.toString(), newPassword.text.toString(), object :
                    Authorization.ChangePasswordCallback {
                    override fun onSuccess() {
                        Toast.makeText(this@ResetPassword, "Pass alterada com sucesso!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(errorMessage: String) {
                        Toast.makeText(this@ResetPassword, "Erro ao alterar pass do perfil: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                })

                dialog.dismiss()
            }
            alertDialogBuilder.setNegativeButton("Não") { dialog, _ ->
                dialog.cancel()
            }
            alertDialogBuilder.show()
        }

        signinLink.setOnClickListener {
            val signinIntent = Intent(this@ResetPassword, MainActivity::class.java)
            startActivity(signinIntent)
        }

        signupLink.setOnClickListener {
            val signupIntent = Intent(this@ResetPassword, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
}