package at.privatepilot.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import at.privatepilot.MainActivity
import at.privatepilot.databinding.LoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel.loginResult.observe(this, Observer { loginResult ->
            if (loginResult) {
                // Login successful, launch MainActivity
                launchMainActivity()
            } else {
                // Login failed, show an error message or handle it as needed
            }
        })

        // Set up your login UI components and handle login button click
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Attempt login
            loginViewModel.attemptLogin(username, password)
        }

        binding.registerTextView.setOnClickListener {
            //TODO DO DA REGISTA
        }
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}