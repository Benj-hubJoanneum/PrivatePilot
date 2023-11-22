package at.privatepilot.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    fun attemptLogin(username: String, password: String) {
        // TODO: Replace this with actual network call for authentication
        // For simplicity, using hardcoded credentials for demonstration
        if (username == "user" && password == "pass") {
            // Successful login
            _loginResult.postValue(true)
        } else {
            // Failed login
            _loginResult.postValue(false)
        }
    }
}