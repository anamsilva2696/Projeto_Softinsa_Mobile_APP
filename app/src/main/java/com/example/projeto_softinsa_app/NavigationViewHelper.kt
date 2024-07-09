import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projeto_softinsa_app.API.Perfil
import com.example.projeto_softinsa_app.ListUser.MainListUsers
import com.example.projeto_softinsa_app.MainActivity
import com.example.projeto_softinsa_app.MainBeneficio
import com.example.projeto_softinsa_app.MainCalendario
import com.example.projeto_softinsa_app.MainIdeia
import com.example.projeto_softinsa_app.MainOferta_Vaga
import com.example.projeto_softinsa_app.MainOportunidades
import com.example.projeto_softinsa_app.MainPage
import com.example.projeto_softinsa_app.MainReporting
import com.example.projeto_softinsa_app.PerfilActivity
import com.example.projeto_softinsa_app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView


object NavigationViewHelper {
    fun setupNavigationView(
        activity: AppCompatActivity,
        drawerLayout: DrawerLayout,
        navigationView: NavigationView
    ) {
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        var isColaborador: Boolean
        var cargoId: Int


        var googleSignInClient: GoogleSignInClient
        val user = Perfil(activity, null)
        isColaborador = user.getStoredIsColaborador()
        cargoId = user.getStoredCargoId()

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent1 = Intent(activity, MainPage::class.java)
                    intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent1)
                }
                R.id.nav_oportunities -> {
                    val intent2 = Intent(activity, MainOportunidades::class.java)
                    activity.startActivity(intent2)
                }
                R.id.nav_ideas -> {
                    val intent3 = Intent(activity, MainIdeia::class.java)
                    activity.startActivity(intent3)
                }
                R.id.nav_job_offers -> {
                    val intent4 = Intent(activity, MainOferta_Vaga::class.java)
                    activity.startActivity(intent4)
                }
                R.id.nav_benefits -> {
                    val intent5 = Intent(activity, MainBeneficio::class.java)
                    activity.startActivity(intent5)
                }
                R.id.nav_list_users -> {
                    if(!isColaborador)
                    {
                        Toast.makeText(activity, "Não tens permissões para aceder à lista de utilizadores.", Toast.LENGTH_SHORT).show()
                    }else{
                        val intent7 = Intent(activity, MainListUsers::class.java)
                        activity.startActivity(intent7)
                    }
                }

                R.id.reporting -> {
                    if(!isColaborador)
                    {
                        Toast.makeText(activity, "Não tens permissões para aceder ao reporting.", Toast.LENGTH_SHORT).show()
                    }else
                    {
                        val intent = Intent(activity, MainReporting::class.java)
                        activity.startActivity(intent)
                    }

                }

                R.id.nav_profile -> {
                    if (cargoId <= 2) {
                        val intent8 = Intent(activity, PerfilActivity::class.java)
                        activity.startActivity(intent8)
                    } else {
                    }
                }

                R.id.nav_calendario -> {
                    val intent9 = Intent(activity, MainCalendario::class.java)
                    activity.startActivity(intent9)
                }
                R.id.nav_logout -> {

                    val sharedPreferences: SharedPreferences = activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    val isGoogleSignIn = sharedPreferences.getBoolean("isGoogleSignIn", false)
                    Log.d("isGoogleSignIn", isGoogleSignIn.toString())
                    if (isGoogleSignIn) {
                        // Configure Google Sign-In
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build()

                        googleSignInClient = GoogleSignIn.getClient(activity, gso)


                        // Call signOut() method
                        googleSignInClient.signOut()
                            .addOnCompleteListener(
                                activity,
                                OnCompleteListener<Void?> { // Handle the sign-out result
                                    Log.d("MainActivity", "Signed out successfully")
                                    editor.clear()
                                    editor.apply()
                                    val intent9 = Intent(activity, MainActivity::class.java)
                                    activity.startActivity(intent9)
                                })
                            .addOnFailureListener { e ->
                                // Handle the error
                                Log.e("MainActivity", "Sign out failed", e)
                            }
                    } else {
                        editor.clear()
                        editor.apply()
                        val intent9 = Intent(activity, MainActivity::class.java)
                        activity.startActivity(intent9)
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val navProfileItem = navigationView.menu.findItem(R.id.nav_profile)
        if (cargoId > 2) {
            navProfileItem.isVisible = false
        }
    }
}
