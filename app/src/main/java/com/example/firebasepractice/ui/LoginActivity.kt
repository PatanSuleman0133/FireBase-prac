package com.example.firebasepractice.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasepractice.R
import com.example.firebasepractice.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        getFCMToken()
        requestPermission()

        binding.tvSignup.setOnClickListener {
            goToRegister()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isEmpty()) {
                binding.etEmail.error = "Email is required"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password is required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }
            RegisterUser(email, password)
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            goToMain()
        }
    }


    // firebase register function
    private fun RegisterUser(email: String, password: String) {
        showLoader(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showLoader(false)
                    goToMain()
                } else {
                    showLoader(false)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // screen loader function
    private fun showLoader(show: Boolean) {
        if (show) {
            binding.screenLoader.visibility = View.VISIBLE
            binding.loginContainer.visibility = View.GONE
        } else {
            binding.screenLoader.visibility = View.GONE
            binding.loginContainer.visibility = View.VISIBLE
        }
    }


    // FCM token generation function
    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful){
                val token = it.result
                Log.d("Token",token)
            }else{
                Log.d("Token","Failed")
            }
        }
    }

    // request permission function
    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),0)
        }
    }

    // navigation function
    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}