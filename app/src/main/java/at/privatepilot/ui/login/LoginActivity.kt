package at.privatepilot.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.privatepilot.MainActivity
import at.privatepilot.databinding.LoginBinding
import at.privatepilot.restapi.client.CredentialManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decryptedUsername = CredentialManager.getStoredUsername(this@LoginActivity)
        val decryptedPassword = CredentialManager.getStoredPassword(this@LoginActivity)

        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val enteredUsername = binding.usernameEditText.text.toString()
            val enteredPassword = binding.passwordEditText.text.toString()

            if (enteredUsername == decryptedUsername && enteredPassword == decryptedPassword) {

                launchMainActivity()
            }
        }

        binding.registerTextView.setOnClickListener {
            launchRegisterActivity()
        }

    if (decryptedUsername == null && decryptedPassword == null)
        launchRegisterActivity()

    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
