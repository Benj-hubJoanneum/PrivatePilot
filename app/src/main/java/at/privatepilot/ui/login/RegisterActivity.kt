package at.privatepilot.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import at.privatepilot.databinding.RegisterBinding
import at.privatepilot.restapi.client.CredentialManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val serverAddress = binding.serverAddressEditText.text.toString()

            CredentialManager.saveUserCredentials(this@RegisterActivity, username, password, serverAddress)

            finish()
        }
    }
}